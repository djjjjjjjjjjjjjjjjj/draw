
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import DrawManager from "./components/DrawManager"

import RaffleManager from "./components/RaffleManager"


import MyPage from "./components/MyPage"
import OrderManager from "./components/OrderManager"

import AuthManager from "./components/AuthManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/draws',
                name: 'DrawManager',
                component: DrawManager
            },

            {
                path: '/raffles',
                name: 'RaffleManager',
                component: RaffleManager
            },


            {
                path: '/myPages',
                name: 'MyPage',
                component: MyPage
            },
            {
                path: '/orders',
                name: 'OrderManager',
                component: OrderManager
            },

            {
                path: '/auths',
                name: 'AuthManager',
                component: AuthManager
            },



    ]
})
