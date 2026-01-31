import { createRouter, createWebHistory } from 'vue-router'
import Home from '../components/Home.vue'
import UserRegister from '../components/user/UserRegister.vue'
import UserLogin from '../components/user/UserLogin.vue'
import UserProfile from '../components/user/UserProfile.vue'
import UserOrderList from '../components/user/UserOrderList.vue'
import UserAddressList from '../components/user/address/UserAddressList.vue'
import UserFavoriteList from '../components/user/favorite/UserFavoriteList.vue'
import UserHistoryList from '../components/user/history/UserHistoryList.vue'
import SellerRegister from '../components/user/SellerRegister.vue'
import SellerLogin from '../components/user/SellerLogin.vue'
import ProductList from '../components/product/ProductList.vue'
import CategoryList from '../components/product/CategoryList.vue'
import SellerProductList from '../components/seller/SellerProductList.vue'
import SellerCategoryList from '../components/seller/SellerCategoryList.vue'
import SellerOrderList from '../components/seller/order/SellerOrderList.vue'
import SellerStatistics from '../components/seller/statistics/SellerStatistics.vue'
import ProductStore from '../components/product/ProductStore.vue'
import Cart from '../components/cart/Cart.vue'
import Order from '../components/order/Order.vue'
import ProductDetail from '../components/product/ProductDetail.vue'
import CommentManagement from '../components/product/CommentManagement.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/register',
    name: 'Register',
    component: UserRegister
  },
  {
    path: '/login',
    name: 'Login',
    component: UserLogin
  },
  {
    path: '/seller/register',
    name: 'SellerRegister',
    component: SellerRegister
  },
  {
    path: '/seller/login',
    name: 'SellerLogin',
    component: SellerLogin
  },
  {
    path: '/profile',
    name: 'Profile',
    component: UserProfile
  },
  {
    path: '/products',
    name: 'Products',
    component: ProductList
  },
  {
    path: '/categories',
    name: 'Categories',
    component: CategoryList
  },
  {
    path: '/store',
    name: 'Store',
    component: ProductStore
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Order
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/product/:id/comments',
    name: 'CommentManagement',
    component: CommentManagement
  },
  // 商家路由
  {
    path: '/seller/products',
    name: 'SellerProducts',
    component: SellerProductList
  },
  {
    path: '/seller/categories',
    name: 'SellerCategories',
    component: SellerCategoryList
  },
  {
    path: '/seller/orders',
    name: 'SellerOrders',
    component: SellerOrderList
  },
  {
    path: '/seller/statistics',
    name: 'SellerStatistics',
    component: SellerStatistics
  },
  // 用户路由
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: UserProfile
  },
  {
    path: '/user/orders',
    name: 'UserOrders',
    component: UserOrderList
  },
  {
    path: '/user/addresses',
    name: 'UserAddresses',
    component: UserAddressList
  },
  {
    path: '/user/favorites',
    name: 'UserFavorites',
    component: UserFavoriteList
  },
  {
    path: '/user/history',
    name: 'UserHistory',
    component: UserHistoryList
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router