package com.iflytek.itsc.auth.service.service;

import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

@Service
public class LdapAuthenticationService {

    @Value("${authentication.ldap.url:}")
    private String ldapUrl;

    @Value("${authentication.ldap.base-dn:}")
    private String baseDn;

    @Value("${authentication.ldap.bind-dn:}")
    private String bindDn;

    @Value("${authentication.ldap.bind-password:}")
    private String bindPassword;

    @Value("${authentication.ldap.user-search-base:}")
    private String userSearchBase;

    @Value("${authentication.ldap.user-search-filter:(sAMAccountName={0})}")
    private String userSearchFilter;

    @Value("${authentication.ldap.connect-timeout-ms:5000}")
    private String connectTimeoutMs;

    @Value("${authentication.ldap.read-timeout-ms:5000}")
    private String readTimeoutMs;

    public void authenticate(String account, String password) {
        if (StringUtils.isAnyBlank(ldapUrl, baseDn)) {
            throw new AuthBizException(ErrorCodeEnum.LDAP_CONFIG_INVALID);
        }
        if (StringUtils.isBlank(password)) {
            throw new AuthBizException(ErrorCodeEnum.LDAP_AUTH_FAILED);
        }
        String userDn = findUserDn(account);
        bindAsUser(userDn, password);
    }

    private String findUserDn(String account) {
        DirContext context = null;
        try {
            context = new InitialDirContext(buildBaseEnv(bindDn, bindPassword));
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String searchBase = StringUtils.defaultIfBlank(userSearchBase, baseDn);
            String filter = buildSearchFilter(account);
            NamingEnumeration<SearchResult> results = context.search(searchBase, filter, controls);
            if (!results.hasMore()) {
                throw new AuthBizException(ErrorCodeEnum.LDAP_AUTH_FAILED);
            }
            SearchResult result = results.next();
            if (results.hasMore()) {
                throw new AuthBizException(ErrorCodeEnum.LDAP_AUTH_FAILED);
            }
            Attributes attributes = result.getAttributes();
            String distinguishedName = attributes != null && attributes.get("distinguishedName") != null
                    ? String.valueOf(attributes.get("distinguishedName").get())
                    : null;
            if (StringUtils.isNotBlank(distinguishedName)) {
                return distinguishedName;
            }
            return result.getNameInNamespace();
        } catch (NamingException e) {
            throw new AuthBizException(ErrorCodeEnum.LDAP_AUTH_FAILED);
        } finally {
            closeQuietly(context);
        }
    }

    private void bindAsUser(String userDn, String password) {
        DirContext context = null;
        try {
            context = new InitialDirContext(buildBaseEnv(userDn, password));
        } catch (NamingException e) {
            throw new AuthBizException(ErrorCodeEnum.LDAP_AUTH_FAILED);
        } finally {
            closeQuietly(context);
        }
    }

    private Hashtable<String, String> buildBaseEnv(String principal, String credentials) {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put("com.sun.jndi.ldap.connect.timeout", connectTimeoutMs);
        env.put("com.sun.jndi.ldap.read.timeout", readTimeoutMs);
        if (StringUtils.isNotBlank(principal)) {
            env.put(Context.SECURITY_PRINCIPAL, principal);
        }
        if (StringUtils.isNotBlank(credentials)) {
            env.put(Context.SECURITY_CREDENTIALS, credentials);
        }
        return env;
    }

    private String buildSearchFilter(String account) {
        return userSearchFilter.replace("{0}", escapeLdapFilter(account));
    }

    private String escapeLdapFilter(String value) {
        return value
                .replace("\\", "\\5c")
                .replace("*", "\\2a")
                .replace("(", "\\28")
                .replace(")", "\\29")
                .replace("\u0000", "\\00");
    }

    private void closeQuietly(DirContext context) {
        if (context == null) {
            return;
        }
        try {
            context.close();
        } catch (NamingException ignored) {
        }
    }
}
