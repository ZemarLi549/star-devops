export default [
  {
    path: "/usermanage",
    component: () => import("@/views/usermanage/layout.vue"),
    children: [
      {
        path: 'space',
        name: '空间',
        component: () => import("@/views/usermanage/space/index.vue")
      }, {
        path: 'pool',
        name: '用户池',
        component: () => import("@/views/usermanage/pool/index.vue")
      },
      {
        path: 'menu',
        name: '菜单',
        component: () => import("@/views/usermanage/menu/index.vue")
      },
      {
        path: 'space/role',
        name: '角色管理',
        component: () => import("@/views/usermanage/role/index.vue")
      },
      {
        path: 'space/user',
        name: '成员管理',
        component: () => import("@/views/usermanage/user/index.vue")
      },
      {
        path: 'space/business',
        name: '业务组',
        component: () => import('@/views/usermanage/businessgroup/index.vue')
      }
    ]
  }
]