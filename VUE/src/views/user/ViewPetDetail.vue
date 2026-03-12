<template>
    <div class="pet-detail-container">

        <!-- 宠物基本信息 -->
        <div class="pet-basic-info">
            <div class="pet-header">
                <h1 class="pet-name">{{ petInfo.name }}</h1>
                <!-- 添加已被领养标签 -->
                <div v-if="isPetAdopted" class="adopted-tag">
                    <el-tag type="danger" size="medium">已被领养</el-tag>
                </div>
            </div>
            <div class="meta-info">
                <span class="pet-type">{{ petInfo.petTypeName }}</span>
                <span class="pet-gender">{{ petInfo.gender | genderFilter }}</span>
                <span class="pet-age">{{ petInfo.age }}个月</span>
            </div>

            <!-- 疫苗状态 -->
            <div class="vaccine-status" :class="{ 'vaccined': petInfo.isVaccine }">
                {{ petInfo.isVaccine ? '已接种疫苗' : '未接种疫苗' }}
            </div>
        </div>

        <!-- 宠物封面图片 -->
        <div class="pet-cover">
            <img :src="getSafeImageUrl(petInfo.cover)" :alt="petInfo.name" class="cover-image" @error="handleImageError">
        </div>

        <!-- 宠物详情描述 -->
        <div class="pet-detail" v-html="petInfo.detail"></div>

        <!-- 领养信息 -->
        <div class="adopt-info">
            <div class="address">
                <i class="el-icon-location-outline"></i>
                <span>{{ petInfo.address }}</span>
            </div>
            
            <!-- 添加申请领养按钮 -->
            <div class="action-area">
                <el-button 
                    v-if="!isPetAdopted" 
                    type="primary" 
                    size="medium" 
                    class="adopt-btn"
                    @click="handleAdoptApply">
                    申请领养
                </el-button>
                <el-button 
                    v-else 
                    type="info" 
                    size="medium" 
                    class="adopt-btn"
                    disabled
                    @click="showAdoptedAlert">
                    已被领养
                </el-button>
            </div>
        </div>

        <!-- 申请领养对话框 -->
        <el-dialog 
            title="申请领养" 
            :visible.sync="applyDialogVisible" 
            width="500px"
            @close="resetApplyForm">
            <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="100px">
                <el-form-item label="证明材料" prop="detail">
                    <el-input
                        type="textarea"
                        :rows="4"
                        v-model="applyForm.detail"
                        placeholder="请详细说明您的领养条件、饲养经验、居住环境等"
                        maxlength="500"
                        show-word-limit>
                    </el-input>
                </el-form-item>
                <el-form-item label="收货地址" prop="addressId">
                    <el-select v-model="applyForm.addressId" placeholder="请选择收货地址" style="width: 100%">
                        <el-option
                            v-for="address in addressList"
                            :key="address.id"
                            :label="`${address.receiver} - ${address.phone} - ${address.detail}`"
                            :value="address.id">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="applyDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitAdoptApply" :loading="submitting">确 定</el-button>
            </span>
        </el-dialog>

    </div>
</template>

<script>
import Evaluations from '@/components/Evaluations.vue';

export default {
    components: { Evaluations },
    data() {
        return {
            petInfo: {},
            queryDto: {},
            applyDialogVisible: false,
            submitting: false,
            addressList: [],
            applyForm: {
                detail: '',
                addressId: null,
                petId: null
            },
            applyRules: {
                detail: [
                    { required: true, message: '请填写证明材料', trigger: 'blur' },
                    { min: 10, message: '证明材料至少10个字符', trigger: 'blur' }
                ],
                addressId: [
                    { required: true, message: '请选择收货地址', trigger: 'change' }
                ]
            }
        }
    },
    filters: {
        genderFilter(val) {
            return val === 1 ? '公' : '母'
        }
    },
    computed: {
        // 计算宠物是否被领养
        isPetAdopted() {
            if (!this.petInfo || this.petInfo.isAdopt === undefined) {
                return false;
            }
            
            const isAdopt = this.petInfo.isAdopt;
            console.log('计算属性判断 isAdopt:', isAdopt, '类型:', typeof isAdopt);
            
            // 处理各种可能的布尔值表示
            if (isAdopt === true || isAdopt === 1 || isAdopt === '1') {
                return true;
            }
            
            // 处理字符串'true'
            if (typeof isAdopt === 'string' && isAdopt.toLowerCase() === 'true') {
                return true;
            }
            
            return false;
        }
    },
    created() {
        this.queryDto = this.$router.currentRoute.query;
        this.fetchPetInfo(this.queryDto.id);
        this.fetchUserAddresses();
    },
    methods: {
        // 查询宠物信息 - 修复版
        async fetchPetInfo(id) {
            try {
                console.log('📡 开始请求宠物详情，ID:', id);
                
                // 使用正确的接口路径：/pet/{id}
                const { data } = await this.$axios.get(`/pet/${id}`);
                
                console.log('🐶 宠物详情响应数据：', data);
                console.log('🔍 isAdopt字段值：', data.isAdopt, '类型：', typeof data.isAdopt);
                console.log('🔍 所有字段：', Object.keys(data));
                
                this.petInfo = data;
                this.applyForm.petId = id;
                
                // 如果宠物已被领养，显示提示信息
                if (this.isPetAdopted) {
                    console.log('⚠️ 检测到宠物已被领养');
                    this.$message.warning('该宠物已被领养，不可重复申请');
                } else {
                    console.log('✅ 宠物可领养');
                }
            } catch (error) {
                console.log("查询宠物详情信息异常：", error);
                this.$message.error('获取宠物信息失败');
            }
        },
        
        // 获取用户地址列表
        async fetchUserAddresses() {
            try {
                const { data } = await this.$axios.get('/address/list');
                this.addressList = data || [];
                if (this.addressList.length === 0) {
                    this.$message.info('请先添加收货地址');
                }
            } catch (error) {
                console.log("获取地址列表异常：", error);
            }
        },
        
        // 处理申请领养
        handleAdoptApply() {
            console.log('=== 点击申请领养按钮 ===');
            console.log('宠物ID:', this.queryDto.id);
            console.log('计算属性 isPetAdopted:', this.isPetAdopted);
            console.log('原始值 petInfo.isAdopt:', this.petInfo.isAdopt);
            console.log('地址列表长度:', this.addressList.length);
            
            // 1. 检查宠物是否已被领养
            if (this.isPetAdopted) {
                console.log('❌ 宠物已被领养，阻止申请');
                this.showAdoptedAlert();
                return;
            }
            
            // 2. 检查是否有收货地址
            if (this.addressList.length === 0) {
                console.log('⚠️ 没有收货地址，提示用户');
                this.$message.warning('请先添加收货地址');
                return;
            }
            
            // 3. 宠物可领养，打开申请表单
            console.log('✅ 宠物可领养，打开申请表单');
            this.applyDialogVisible = true;
        },
        
        // 显示已被领养提示
        showAdoptedAlert() {
            this.$alert('该宠物已被其他用户领养，请选择其他可领养的宠物', '宠物已被领养', {
                confirmButtonText: '确定',
                type: 'warning',
                center: true,
                customClass: 'adopted-alert'
            });
        },
        
        // 提交领养申请
        async submitAdoptApply() {
            try {
                // 表单验证
                const valid = await this.$refs.applyFormRef.validate();
                if (!valid) return;
                
                this.submitting = true;
                
                // 再次前端验证：检查宠物是否已被领养
                if (this.isPetAdopted) {
                    this.$message.warning('该宠物已被领养，不可重复申请');
                    this.applyDialogVisible = false;
                    return;
                }
                
                // 发送领养申请
                const response = await this.$axios.post('/pet-adopt-order/save', {
                    petId: this.applyForm.petId,
                    addressId: this.applyForm.addressId,
                    detail: this.applyForm.detail
                });
                
                if (response.code === 200) {
                    this.$message.success(response.message || '申请提交成功，请等待审核');
                    this.applyDialogVisible = false;
                    
                    // 刷新宠物信息（isAdopt状态可能已更新）
                    this.fetchPetInfo(this.applyForm.petId);
                } else {
                    this.$message.error(response.message || '申请提交失败');
                }
            } catch (error) {
                console.log("提交领养申请异常：", error);
                
                // 处理后端返回的错误信息
                if (error.response && error.response.data) {
                    const errorData = error.response.data;
                    this.$message.error(errorData.message || '申请提交失败');
                    
                    // 如果是宠物已被领养，刷新页面状态
                    if (errorData.message && errorData.message.includes('已被领养')) {
                        this.fetchPetInfo(this.applyForm.petId);
                    }
                } else {
                    this.$message.error('网络错误，请稍后重试');
                }
            } finally {
                this.submitting = false;
            }
        },
        
        // 重置申请表单
        resetApplyForm() {
            this.applyForm.detail = '';
            if (this.$refs.applyFormRef) {
                this.$refs.applyFormRef.resetFields();
            }
        },
        
        // 图片安全处理（基于历史记忆）
        getSafeImageUrl(url) {
            if (!url || url === 'undefined' || url === 'null') {
                return this.getDefaultPetImage();
            }
            if (url.startsWith('http') || url.startsWith('data:') || url.startsWith('/api/')) {
                return url;
            }
            if (url.startsWith('/')) {
                return url;
            }
            return this.getDefaultPetImage();
        },
        
        // 默认宠物图片（基于历史记忆）
        getDefaultPetImage() {
            return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgdmlld0BveD0iMCAwIDEwMCAxMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iNTAiIGN5PSI1MCIgcj0iNDUiIGZpbGw9IiNFNkU2RTYiLz48cGF0aCBkPSJNNjUgNjBDNjUgNjYuNTcgNjAuNTcgNzEgNTQgNzFIMzZDMjkuNDMgNzEgMjUgNjYuNTcgMjUgNjBWNTFDMjUgNDQuNDMgMjkuNDMgNDAgMzYgNDBINTRDNjAuNTcgNDAgNjUgNDQuNDMgNjUgNTFWNjBaIiBmaWxsPSIjRkZGRkZGIi8+PGNpcmNsZSBjeD0iNDAiIGN5PSI1MSIgcj0iMyIgZmlsbD0iIzY5NjA1RCIvPjxjaXJjbGUgY3g9IjYwIiBjeT0iNTEiIHI9IjMiIGZpbGw9IiM2OTYwNUQiLz48L3N2Zz4=';
        },
        
        // 图片错误处理
        handleImageError(event) {
            event.target.src = this.getDefaultPetImage();
            event.target.onerror = null;
        }
    }
}
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
.pet-detail-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
}

.pet-header {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 10px;
}

.pet-name {
    font-size: 42px;
    color: #333;
    margin: 0;
}

.adopted-tag {
    margin-top: 5px;
}

.cover-image {
    width: 100%;
    max-height: 400px;
    object-fit: cover;
    border-radius: 8px;
}

.pet-basic-info {
    margin: 20px 0;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
}

.meta-info {
    display: flex;
    gap: 15px;
    color: #666;
    font-size: 14px;
    margin-bottom: 10px;
}

.vaccine-status {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    background: #fef0f0;
    color: #f56c6c;
}

.vaccine-status.vaccined {
    background: #f0f9eb;
    color: #67c23a;
}

.pet-detail {
    margin: 20px 0;
    line-height: 1.6;
    color: #666;
}

.pet-detail>>>ul {
    padding-left: 20px;
}

.pet-detail>>>li {
    margin-bottom: 8px;
}

.adopt-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 30px;
    padding-top: 15px;
    padding-bottom: 15px;
    background-color: rgb(230, 248, 248);
    padding-inline: 20px;
    border-radius: 8px;
}

.address {
    color: #666;
    font-size: 14px;
}

.address i {
    margin-right: 5px;
}

.action-area {
    display: flex;
    align-items: center;
}

.adopt-btn {
    width: 120px;
}

/* 已被领养提示框样式 */
.adopted-alert {
    .el-message-box__status {
        color: #F56C6C;
    }
    .el-message-box__title {
        color: #F56C6C;
        font-weight: bold;
    }
}

/* 已被领养按钮样式增强 */
.adopt-btn[disabled] {
    cursor: not-allowed;
    opacity: 0.7;
}

.adopt-btn[disabled]:hover {
    background-color: #C0C4CC;
    border-color: #C0C4CC;
    color: #FFF;
}
</style>