<template>
    <div class="app-container">
        <!-- 顶部导航栏 -->
        <header class="app-header">
            <div class="header-content">
                <div class="main-nav">
                    <Logo class="logo" :logoSize="Number(46)" :fontSize="Number(20)" textColor="rgb(51,51,51)" sysName="宠物领养守护系统" />
                </div>
                <nav class="main-nav">
                    <router-link v-for="item in navItems" :key="item.path" :to="item.path" class="nav-item"
                        active-class="active" @click="closeMobileMenu">
                        <i style="margin-right: 6px;" :class="item.icon"></i>
                        {{ item.title }}
                        <span class="nav-indicator"></span>
                    </router-link>
                    <!-- 发布宠物经验帖子按钮 -->
                    <div @click="goPetPost" class="publish-pet-post">
                        <i class="el-icon-document"></i>
                        社区发帖
                    </div>
                    <el-dropdown trigger="click">
                        <span class="user-dropdown-trigger">
                            <div class="user-info">
                                <img :src="userInfo.avatar" alt="用户头像" class="user-avatar">
                                <span class="username">{{ userInfo.username }}</span>
                                <i style="color: rgb(51,51,51);" class="el-icon-arrow-down dropdown-icon"></i>
                            </div>
                        </span>
                        <el-dropdown-menu slot="dropdown" class="user-dropdown">
                            <el-dropdown-item @click.native="center" icon="el-icon-s-data">
                                创作中心
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="address" icon="el-icon-s-ticket">
                                收货地址
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="updateUserInfo" icon="el-icon-user-solid">
                                修改信息
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="dialogPasswordOperation = true" icon="el-icon-finished">
                                修改密码
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="proposal" icon="el-icon-connection">
                                意见反馈
                            </el-dropdown-item>
                            <el-dropdown-item @click.native="dialogOutOperation = true" icon="el-icon-s-fold">
                                退出登录
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </nav>
            </div>
        </header>

        <!-- 主内容区 -->
        <main class="app-main">
            <router-view></router-view>
        </main>

        <!-- 退出登录弹窗 -->
        <el-dialog title="退出登录" :show-close="false" :visible.sync="dialogOutOperation" width="20%">
            <span>确定退出登录？</span>
            <span slot="footer" class="dialog-footer">
                <span class="primary-bt" @click="dialogOutOperation = false">取消</span>
                <span class="info-bt" @click="confirmLoginOut">确定</span>
            </span>
        </el-dialog>

        <!-- 修改密码弹窗 -->
        <el-dialog title="修改密码" :closeOnClickModal="false" :show-close="false" :visible.sync="dialogPasswordOperation"
            width="28%">
            <div>
                <div>
                    <p>*原始密码</p>
                    <el-input style="height: 60px;line-height: 60px;" show-password placeholder="输入" type="password"
                        v-model="oldPassword" clearable>
                    </el-input>
                </div>
                <div>
                    <p>*新密码</p>
                    <el-input style="height: 60px;line-height: 60px;" show-password placeholder="输入" type="password"
                        v-model="newPassword" clearable>
                    </el-input>
                </div>
                <div>
                    <p>*确认密码</p>
                    <el-input style="height: 60px;line-height: 60px;" show-password placeholder="输入" type="password"
                        v-model="againPassword" clearable>
                    </el-input>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <span class="primary-bt" @click="dialogPasswordOperation = false">取消</span>
                <span class="info-bt" @click="confirmUpdatePassword">确定</span>
            </span>
        </el-dialog>

        <!-- 用户信息操作弹窗 -->
        <el-dialog title="修改信息" :show-close="false" :visible.sync="dialogUserInfoVisible" :closeOnClickModal="false"
            width="35%">
            <el-tabs v-model="tabActiveName" :tab-position="tabPosition" style="height: 420px;">
                <el-tab-pane label="核心信息" name="first">
                    <div>
                        <div class="user-avatar-container">
                            <p>点击📷处即可上传头像</p>
                            <img v-if="avatar" :src="avatar || ''" alt="用户头像" class="user-avatar-large">
                            <el-upload style="margin-left: 20px;" class="avatar-uploader"
                                action="http://localhost:21090/api/v1.0/pet-adopt-api/file/upload" :show-file-list="false"
                                :on-success="handleImageSuccess">
                                <i class="el-icon-camera-solid upload-icon"></i>
                            </el-upload>
                        </div>
                        <div class="form-group">
                            <p>*用户账号</p>
                            <el-input :disabled="true" placeholder="输入" v-model="apiParam.account" clearable>
                            </el-input>
                        </div>
                        <div class="form-group">
                            <p>*用户名</p>
                            <el-input placeholder="输入" v-model="apiParam.username" clearable>
                            </el-input>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane label="基本信息" name="second">
                    <div>
                        <div class="form-group">
                            <p>用户性别</p>
                            <Tab :buttons="[
                                { label: '女', value: '1' },
                                { label: '男', value: '2' }
                            ]" :initialActive="String(apiParam.gender) || '2'" @change="handleGenderChange" />
                        </div>
                        <div class="form-group">
                            <p>电子邮件</p>
                            <el-input placeholder="输入" v-model="apiParam.email" clearable>
                            </el-input>
                        </div>
                        <div class="form-group">
                            <p>联系电话</p>
                            <el-input placeholder="输入" v-model="apiParam.phone" clearable>
                            </el-input>
                        </div>
                        <div class="form-group">
                            <p>出生年月</p>
                            <el-date-picker style="width: 100%;" v-model="apiParam.birthday" type="date"
                                placeholder="选择日期">
                            </el-date-picker>
                        </div>
                    </div>
                </el-tab-pane>
            </el-tabs>
            <span slot="footer" class="dialog-footer">
                <span class="primary-bt" @click="dialogUserInfoVisible = false">取消</span>
                <span class="info-bt" @click="handleConfirm">
                    确定修改
                </span>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import Logo from "@/components/Logo"
import AutoInput from '@/components/AutoInput.vue';
import Tab from "@/components/Tab"
import {
    setUserInfo,
    getUserInfo,
    clearToken,
    clearRole,
    clearUserInfo 
} from "@/utils/storage"

export default {
    components: { Logo, AutoInput, Tab },
    name: "AppLayout",
    data() {
        return {
            oldPassword: '', // 原始密码
            newPassword: '', // 新密码
            againPassword: '', // 确认密码
            tabActiveName: 'first', // 修改信息Tab默认选中第一项
            tabPosition: 'right', // 修改信息Tab靠右
            dialogPasswordOperation: false, // 修改密码弹窗开关
            dialogUserInfoVisible: false, // 修改信息弹窗开关
            dialogOutOperation: false, // 退出登录弹窗控制开关
            navItems: [
                { path: '/pet-list', icon: '', title: '宠物领养大厅' },
                { path: '/my-pet-adopt-order', icon: 'el-icon-tickets', title: '宠物领养订单' },
                { path: '/pet-health', icon: 'el-icon-first-aid-kit', title: '宠物健康档案' }, 
                { path: '/pet-post', icon: 'el-icon-s-help', title: '养宠社区' },
                { path: '/warning-questionnaire', icon: 'el-icon-edit', title: '预警问卷' },
                { path: '/notice', icon: '', title: '公告' },
                
                
            ],
            userInfo: {},
            isAuthChecked: false,
            isMobileMenuOpen: false,
            apiParam: {},
            avatar: '', // 用户头像
        }
    },
    async created() {
        await this.handleAuthentication()
        if (this.isAuthChecked) {
            this.initRouteHandling()
        }
    },
    methods: {
        // 加载意见与反馈中心
        proposal() {
            this.$router.push('/proposal-feedback');
        },
        center() {
            // 加载创作中心页面
            this.$router.push('/pet-post-user');
        },
        address() {
            // 加载收货地址页面
            this.$router.push('/address');
        },
        // 跳转至宠物经验帖子发布页
        goPetPost() {
           this.$router.push('/create-pet-post');
        },
        // 性别选择切换
        handleGenderChange(obj) {
            this.apiParam.gender = Number(obj.value);
        },
        handleImageSuccess(res, file) {
            this.$notify({
                title: '头像上传',
                type: res.code === 200 ? 'success' : 'error',
                message: res.code === 200 ? '上传成功' : res.data,
                position: 'bottom-right',
                duration: 1000,
            })
            if (res.code === 200) {
                this.avatar = res.data;
            }
        },
        updateUserInfo() {
            this.avatar = this.userInfo.avatar;
            this.dialogUserInfoVisible = true;
        },
        // 修改信息
        async handleConfirm() {
            try {
                this.apiParam.avatar = this.avatar;
                const { data, message } = await this.$axios.put('/user/update', this.apiParam);
                this.apiParam = data;
                this.$message.success(message);
                this.handleAuthentication();
                this.dialogUserInfoVisible = false; // 关闭弹窗
            } catch (error) {
                this.$message.warning(error.message);
            }
        },
        // 修改密码
        async confirmUpdatePassword() {
            if (!this.oldPassword || !this.newPassword || !this.againPassword) {
                this.$message('原始密码、新密码、确认密码不能为空哦');
                return;
            }
            if (this.newPassword !== this.againPassword) {
                this.$message('前后密码输入不一致');
                return;
            }
            const updatePasswordDto = {
                oldPassword: this.$md5(this.$md5(this.oldPassword)),
                newPassword: this.$md5(this.$md5(this.newPassword)),
                againPassword: this.$md5(this.$md5(this.againPassword))
            }
            try {
                const { message } = await this.$axios.put('/user/updatePassword', updatePasswordDto);
                this.$notify.success({
                    title: '密码修改',
                    message: message,
                    duration: 1000,
                    position: 'bottom-right'
                })
                this.confirmLoginOut();
            } catch (error) {
                this.$message.error(error.message);
            }
        },

        confirmLoginOut() {
            clearToken();
            clearRole();
            clearUserInfo();  // 清除用户信息
            this.$message.success('退出登录成功');  
            this.$router.push('/login');
        },
        
        closeMobileMenu() {
            this.isMobileMenuOpen = false;
        },
        async handleAuthentication() {
            if (this.$route.path === '/login') {
                this.isAuthChecked = true;
                return;
            }

            try {
                const localUser = getUserInfo();
                if (localUser && localUser.token) {
                    this.userInfo = localUser;
                    this.isAuthChecked = true;
                    return;
                }

                const data = await this.$axios.get('/user/auth');
                if (data.code === 200) {
                    this.userInfo = data.data;
                    this.apiParam = { ...data.data };
                    setUserInfo(this.userInfo);
                    this.isAuthChecked = true;
                } else {
                    this.redirectToLogin();
                }
            } catch (error) {
                console.error('认证失败:', error);
                this.redirectToLogin();
            }
        },
        initRouteHandling() {
            this.setupRouteWatcher();
            if (!this.isValidRoute(this.$route.path)) {
                this.$router.replace('/pet-list');
            }
            if (this.$route.path === '/') {
                this.$router.replace('/pet-list');
            }
        },
        isValidRoute(path) {
            return this.navItems.some(item => item.path === path);
        },
        setupRouteWatcher() {
            this.$router.afterEach((to) => {
                if (this.isValidRoute(to.path)) {
                    // 可以在这里记录访问路径
                }
            });
        },
        redirectToLogin() {
            if (this.$route.path !== '/login') {
                this.$notify({
                    type: 'error',
                    title: '安全认证',
                    message: '请重新登录',
                    duration: 2000,
                    position: 'bottom-right'
                });
                this.$router.replace('/login');
            }
        }
    }
}
</script>

<style scoped lang="scss">
.publish-pet-post {
    background-color: rgb(26, 147, 62);
    color: rgb(255, 255, 255);
    padding: 6px 20px;
    border-radius: 25px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(26, 147, 62, 0.2);
    
    /* 强制内容不换行 */
    white-space: nowrap;
    
    /* 确保图标和文字在一行 */
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;

    i {
        font-size: 16px;
        margin-right: 0;  /* 移除原有的右边距 */
        display: inline-block;
    }

    &:hover {
        background-color: rgb(26, 134, 58);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(26, 147, 62, 0.3);
    }
}

.app-container {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: rgb(255, 255, 255);
    overflow-x: hidden;
}

.app-header {
    background-color: rgb(255, 255, 255);
    padding-inline: 100px;
    margin-bottom: 10px;
    z-index: 100;
    position: sticky;
    top: 0;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);  /* 添加轻微阴影 */
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 0 auto;
    padding: 0 40px;
    height: 80px;  /* 增加导航栏高度 */
    position: relative;
}

.logo {
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: transform 0.3s ease;
}

.main-nav {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 24px;  /* 增加导航项间距 */
}

.nav-item {
    color: rgb(51, 51, 51);
    font-size: 15px;  /* 增大字体 */
    font-weight: 500;  /* 加粗字体 */
    text-decoration: none;
    height: 80px;  /* 与header高度一致 */
    line-height: 80px;
    padding: 0 20px;  /* 增加左右内边距，增加宽度 */
    position: relative;
    transition: all 0.2s ease;
    white-space: nowrap;
    display: flex;
    align-items: center;
    justify-content: center;
    letter-spacing: 0.5px;  /* 增加字间距 */

    i {
        font-size: 18px;  /* 增大图标 */
        transition: transform 0.2s ease;
    }

    /* 悬停效果 */
    &:hover {
        color: #1a933e;
        background-color: rgba(26, 147, 62, 0.03);  /* 浅绿色背景 */
        
        i {
            transform: scale(1.1);
        }
    }

    /* 激活状态 */
    &.active {
        color: #1a933e;
        font-weight: 600;

        i {
            color: #1a933e;
        }
    }

    /* 底部高亮条 */
    .nav-indicator {
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 0;
        height: 3px;
        background: linear-gradient(90deg, #1a933e, #2ecc71);
        border-radius: 3px 3px 0 0;
        transition: width 0.3s ease;
    }

    /* 悬停时显示高亮条 */
    &:hover .nav-indicator {
        width: 60%;
    }

    /* 激活时显示高亮条 */
    &.active .nav-indicator {
        width: 80%;
    }
}

/* 移除旧的after伪元素 */
.nav-item::after {
    display: none;
}

.input {
    width: 200px;  /* 增加搜索框宽度 */
    transition: width 0.3s ease;
}

.user-dropdown-trigger {
    cursor: pointer;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 10px;  /* 增加间距 */
    margin-left: 20px;  /* 增加左边距 */
    cursor: pointer;
    padding: 8px 20px;  /* 增加内边距 */
    border-radius: 30px;  /* 增大圆角 */
    position: relative;
    transition: all 0.2s ease;
    background-color: rgba(26, 147, 62, 0.05);  /* 浅绿色背景 */
    border: 1px solid rgba(26, 147, 62, 0.1);  /* 添加边框 */

    &:hover {
        background-color: rgba(26, 147, 62, 0.1);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }
}

.user-avatar {
    width: 30px;  /* 增大头像 */
    height: 30px;
    border-radius: 50%;
    object-fit: cover;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
    border: 2px solid #1a933e;  /* 添加绿色边框 */
}

.username {
    font-size: 15px;  /* 增大字体 */
    color: rgb(51, 51, 51);
    font-weight: 500;
}

.dropdown-icon {
    font-size: 14px;  /* 增大图标 */
    margin-left: 6px;
    transition: transform 0.3s ease;
    color: #1a933e !important;  /* 改为绿色 */
}

.user-info:hover .dropdown-icon {
    transform: rotate(180deg);
}

.user-dropdown {
    border-radius: 12px;  /* 增大圆角 */
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
    padding: 8px 0;
    animation: dropdownFadeIn 0.3s ease;
    border: none;
}

.dropdown-item {
    display: flex;
    align-items: center;
    padding: 12px 28px;  /* 增加内边距 */
    color: #4a5568;
    text-decoration: none;
    transition: all 0.2s ease;
    font-size: 15px;  /* 增大字体 */

    i {
        margin-right: 14px;
        font-size: 18px;  /* 增大图标 */
        color: #1a933e;  /* 改为绿色 */
        transition: transform 0.2s ease;
    }

    &:hover {
        background-color: #f0f7f0;
        color: #1a933e;
        padding-left: 32px;  /* 增加左内边距 */
        
        i {
            transform: scale(1.2);
        }
    }
}

.app-main {
    flex: 1;
    margin-inline: 120px;
    box-sizing: border-box;
    padding: 20px 30px;  /* 增加上下内边距 */
    background-color: rgb(255, 255, 255);
    border-radius: 8px;
    min-height: calc(100vh - 100px);  /* 确保最小高度 */
}

.user-avatar-container {
    text-align: center;
    margin-bottom: 30px;

    p {
        font-size: 14px;
        color: #6b7280;
        margin-bottom: 15px;
    }
}

.user-avatar-large {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 15px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;

    &:hover {
        transform: scale(1.05);
    }
}

.avatar-uploader {
    display: inline-block;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background-color: rgba(26, 147, 62, 0.1);  /* 改为绿色 */
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: -20px;

    i {
        color: #1a933e;  /* 改为绿色 */
        font-size: 18px;
    }

    &:hover {
        background-color: rgba(26, 147, 62, 0.2);
        transform: scale(1.1);
    }
}

.upload-icon {
    color: #1a933e;  /* 改为绿色 */
}

.form-group {
    margin-bottom: 24px;

    p {
        font-size: 14px;
        color: #4b5563;
        margin-bottom: 8px;
        font-weight: 500;
    }

    .el-input {
        width: 100%;
        border-radius: 8px;
        transition: border-color 0.3s ease, box-shadow 0.3s ease;

        &:focus-within {
            border-color: #1a933e;
            box-shadow: 0 0 0 2px rgba(26, 147, 62, 0.2);
        }
    }
}

/* 动画定义 */
@keyframes dropdownFadeIn {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 响应式调整 */
@media (max-width: 1200px) {
    .app-header {
        padding-inline: 50px;
    }
    
    .main-nav {
        gap: 16px;
    }
    
    .nav-item {
        padding: 0 12px;
        font-size: 14px;
    }
}

@media (max-width: 992px) {
    .app-header {
        padding-inline: 20px;
    }
    
    .header-content {
        padding: 0 20px;
    }
    
    .main-nav {
        gap: 10px;
    }
}
</style>