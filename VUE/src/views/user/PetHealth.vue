<template>
    <div class="pet-health-container">
        <!-- 页面标题和操作按钮 -->
        <div class="header">
            <h1>🐾 宠物健康档案</h1>
            <div class="header-actions">
                <el-button type="primary" @click="showAddDialog" icon="el-icon-plus" size="medium">
                    添加健康记录
                </el-button>
            </div>
        </div>

        <!-- 快速统计卡片 -->
        <div class="stat-cards">
            <el-row :gutter="20">
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon" style="background-color: #409eff;">
                            <i class="el-icon-date"></i>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ statData.totalRecords || 0 }}</div>
                            <div class="stat-label">总记录数</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon" style="background-color: #67c23a;">
                            <i class="el-icon-sunny"></i>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ statData.avgWeight ? statData.avgWeight.toFixed(1) : '0.0' }}kg</div>
                            <div class="stat-label">平均体重</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon" style="background-color: #e6a23c;">
                            <i class="el-icon-food"></i>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ statData.goodHealthCount || 0 }}</div>
                            <div class="stat-label">健康状态良好</div>
                        </div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="stat-card">
                        <div class="stat-icon" style="background-color: #f56c6c;">
                            <i class="el-icon-warning"></i>
                        </div>
                        <div class="stat-content">
                            <div class="stat-value">{{ statData.petsCount || 0 }}</div>
                            <div class="stat-label">宠物总数</div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 筛选和搜索区域 -->
        <div class="filter-section">
            <el-card shadow="never">
                <el-form :inline="true" :model="queryDto" class="filter-form">
                    <el-form-item label="宠物选择">
                        <el-select v-model="queryDto.petId" placeholder="请选择宠物" clearable @change="handleFilterChange">
                            <el-option label="全部宠物" :value="null"></el-option>
                            <el-option v-for="pet in myPets" :key="pet.id" :label="pet.name" :value="pet.id">
                                <div style="display: flex; align-items: center;">
                                    <img :src="getSafeImageUrl(pet.cover)" 
                                         @error="handleImageError"
                                         style="width: 20px; height: 20px; border-radius: 50%; margin-right: 8px;">
                                    {{ pet.name }}
                                </div>
                            </el-option>
                        </el-select>
                    </el-form-item>
                    
                    <el-form-item label="身体状况">
                        <el-select v-model="queryDto.bodyCondition" placeholder="身体状况" clearable @change="handleFilterChange">
                            <el-option label="全部" :value="null"></el-option>
                            <el-option v-for="item in conditionOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    
                    <el-form-item label="食欲情况">
                        <el-select v-model="queryDto.appetite" placeholder="食欲情况" clearable @change="handleFilterChange">
                            <el-option label="全部" :value="null"></el-option>
                            <el-option v-for="item in appetiteOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    
                    <el-form-item label="记录日期">
                        <el-date-picker
                            v-model="dateRange"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="yyyy-MM-dd"
                            @change="handleDateChange">
                        </el-date-picker>
                    </el-form-item>
                    
                    <el-form-item>
                        <el-button type="primary" @click="fetchHealthRecords" icon="el-icon-search">查询</el-button>
                        <el-button @click="resetFilter" icon="el-icon-refresh">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
        </div>

        <!-- 图表展示区域 -->
        <div class="chart-section">
            <el-row :gutter="20">
                <el-col :span="16">
                    <el-card shadow="never" style="margin-bottom: 20px;">
                        <template #header>
                            <div class="card-header">
                                <span>📈 体重变化趋势</span>
                                <el-select v-model="chartPetId" placeholder="选择宠物" size="small" style="width: 150px;" @change="updateWeightChart">
                                    <el-option label="全部宠物" :value="null"></el-option>
                                    <el-option v-for="pet in myPets" :key="pet.id" :label="pet.name" :value="pet.id"></el-option>
                                </el-select>
                            </div>
                        </template>
                        <LineChart 
                            :height="'300px'" 
                            :values="weightChartData" 
                            :date="weightXAxis" 
                            :tag="'体重变化趋势'"
                            :subtext="'体重(kg)'"/>
                        <div v-if="weightChartData.length === 0" class="empty-chart">
                            <el-empty description="暂无体重记录数据" :image-size="100"></el-empty>
                        </div>
                    </el-card>
                </el-col>
                
                <el-col :span="8">
                    <el-card shadow="never">
                        <template #header>
                            <div class="card-header">
                                <span>📊 健康状态分布</span>
                            </div>
                        </template>
                        <PieCharts v-if="healthDistribution.length > 0" :height="'300px'" :data="healthDistribution"/>
                        <div v-else class="empty-chart">
                            <el-empty description="暂无健康状态数据" :image-size="100"></el-empty>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </div>

        <!-- 健康记录列表 -->
        <div class="record-section">
            <el-card shadow="never">
                <template #header>
                    <div class="card-header">
                        <span>📋 健康记录列表</span>
                        <div class="header-actions">
                            <el-tag type="info">共 {{ total }} 条记录</el-tag>
                        </div>
                    </div>
                </template>
                
                <!-- 空状态 -->
                <div v-if="healthRecords.length === 0" class="empty-state">
                    <el-empty description="暂无健康记录">
                        <el-button type="primary" @click="showAddDialog" icon="el-icon-plus">添加第一条记录</el-button>
                    </el-empty>
                </div>
                
                <!-- 记录卡片列表 -->
                <div v-else class="record-list">
                    <div v-for="record in healthRecords" :key="record.id" class="record-card">
                        <div class="record-header">
                            <div class="pet-info">
                                <img :src="getPetCover(record.petId)" 
                                     @error="handleImageError"
                                     class="pet-avatar" 
                                     alt="宠物头像">
                                <div class="pet-details">
                                    <div class="pet-name">{{ getPetName(record.petId) }}</div>
                                    <div class="record-meta">
                                        <el-tag size="mini" type="info">{{ formatDate(record.recordDate) }}</el-tag>
                                        <el-tag size="mini" :type="getConditionTagType(record.bodyCondition)">
                                            {{ getConditionText(record.bodyCondition) }}
                                        </el-tag>
                                    </div>
                                </div>
                            </div>
                            <div class="record-actions">
                                <el-button size="mini" icon="el-icon-edit" @click="editRecord(record)">编辑</el-button>
                                <el-button size="mini" icon="el-icon-delete" type="danger" @click="deleteRecord(record.id)">删除</el-button>
                            </div>
                        </div>
                        
                        <div class="record-content">
                            <el-row :gutter="20">
                                <el-col :span="6">
                                    <div class="health-indicator">
                                        <div class="indicator-label">体重</div>
                                        <div class="indicator-value">
                                            <span v-if="record.weight" class="value-number">{{ record.weight }}kg</span>
                                            <span v-else class="value-empty">未记录</span>
                                        </div>
                                    </div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="health-indicator">
                                        <div class="indicator-label">身高</div>
                                        <div class="indicator-value">
                                            <span v-if="record.height" class="value-number">{{ record.height }}cm</span>
                                            <span v-else class="value-empty">未记录</span>
                                        </div>
                                    </div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="health-indicator">
                                        <div class="indicator-label">食欲情况</div>
                                        <div class="indicator-value">
                                            <el-tag size="small" :type="getAppetiteTagType(record.appetite)">
                                                {{ getAppetiteText(record.appetite) }}
                                            </el-tag>
                                        </div>
                                    </div>
                                </el-col>
                                <el-col :span="6">
                                    <div class="health-indicator">
                                        <div class="indicator-label">活力水平</div>
                                        <div class="indicator-value">
                                            <el-tag size="small" :type="getEnergyTagType(record.energyLevel)">
                                                {{ getEnergyText(record.energyLevel) }}
                                            </el-tag>
                                        </div>
                                    </div>
                                </el-col>
                            </el-row>
                            
                            <div v-if="record.notes" class="record-notes">
                                <div class="notes-label">
                                    <i class="el-icon-edit-outline"></i> 备注
                                </div>
                                <div class="notes-content">{{ record.notes }}</div>
                            </div>
                        </div>
                        
                        <div class="record-footer">
                            <div class="record-time">
                                <i class="el-icon-time"></i>
                                创建时间：{{ formatDateTime(record.createTime) }}
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 分页 -->
                <div class="pagination-wrapper" v-if="total > 0">
                    <el-pagination
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                        :current-page="queryDto.current"
                        :page-sizes="[10, 20, 30, 50]"
                        :page-size="queryDto.size"
                        layout="total, sizes, prev, pager, next, jumper"
                        :total="total">
                    </el-pagination>
                </div>
            </el-card>
        </div>

        <!-- 添加/编辑对话框 -->
        <el-dialog 
            :title="dialogTitle" 
            :visible.sync="dialogVisible" 
            width="600px" 
            @close="resetForm"
            :close-on-click-modal="false">
            
            <el-form ref="healthForm" :model="formData" :rules="rules" label-width="100px">
                <el-form-item label="选择宠物" prop="petId" required>
                    <el-select v-model="formData.petId" placeholder="请选择宠物" style="width: 100%;">
                        <el-option v-for="pet in myPets" :key="pet.id" :label="pet.name" :value="pet.id">
                            <div style="display: flex; align-items: center;">
                                <img :src="getSafeImageUrl(pet.cover)" 
                                     @error="handleImageError"
                                     style="width: 25px; height: 25px; border-radius: 50%; margin-right: 10px;">
                                <span>{{ pet.name }}</span>
                            </div>
                        </el-option>
                    </el-select>
                </el-form-item>
                
                <el-form-item label="记录日期" prop="recordDate" required>
                    <el-date-picker
                        v-model="formData.recordDate"
                        type="date"
                        placeholder="选择记录日期"
                        value-format="yyyy-MM-dd"
                        style="width: 100%;">
                    </el-date-picker>
                </el-form-item>
                
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="体重(kg)">
                            <el-input-number 
                                v-model="formData.weight" 
                                :min="0" 
                                :max="100" 
                                :step="0.1" 
                                placeholder="体重"
                                style="width: 100%;">
                            </el-input-number>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="身高(cm)">
                            <el-input-number 
                                v-model="formData.height" 
                                :min="0" 
                                :max="200" 
                                :step="0.1" 
                                placeholder="身高"
                                style="width: 100%;">
                            </el-input-number>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-row :gutter="20">
                    <el-col :span="8">
                        <el-form-item label="身体状况" prop="bodyCondition">
                            <el-select v-model="formData.bodyCondition" placeholder="身体状况" style="width: 100%;">
                                <el-option v-for="item in conditionOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="食欲情况" prop="appetite">
                            <el-select v-model="formData.appetite" placeholder="食欲情况" style="width: 100%;">
                                <el-option v-for="item in appetiteOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="活力水平" prop="energyLevel">
                            <el-select v-model="formData.energyLevel" placeholder="活力水平" style="width: 100%;">
                                <el-option v-for="item in energyOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-form-item label="备注">
                    <el-input
                        type="textarea"
                        v-model="formData.notes"
                        :rows="3"
                        placeholder="请输入其他备注信息（如：疫苗情况、异常行为、就医记录等）"
                        maxlength="500"
                        show-word-limit>
                    </el-input>
                </el-form-item>
            </el-form>
            
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false" size="medium">取消</el-button>
                <el-button type="primary" @click="submitForm" size="medium" :loading="submitLoading">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import LineChart from '@/components/LineChart.vue';
import PieCharts from '@/components/PieCharts.vue';

export default {
    name: 'PetHealth',
    components: { LineChart, PieCharts },
    data() {
        return {
            queryDto: {
                petId: null,
                bodyCondition: null,
                appetite: null,
                startDate: '',
                endDate: '',
                current: 1,
                size: 10
            },
            dateRange: [],
            total: 0,
            healthRecords: [],
            myPets: [],
            
            // 对话框相关
            dialogVisible: false,
            dialogTitle: '添加健康记录',
            submitLoading: false,
            formData: {
                id: null,
                petId: null,
                recordDate: '',
                weight: null,
                height: null,
                bodyCondition: null,
                appetite: null,
                energyLevel: null,
                notes: ''
            },
            rules: {
                petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
                recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }],
                bodyCondition: [{ required: true, message: '请选择身体状况', trigger: 'change' }],
                appetite: [{ required: true, message: '请选择食欲情况', trigger: 'change' }],
                energyLevel: [{ required: true, message: '请选择活力水平', trigger: 'change' }]
            },
            
            // 图表相关
            chartPetId: null,
            weightChartData: [],
            weightXAxis: [],
            healthDistribution: [],
            
            // 统计相关
            statData: {
                totalRecords: 0,
                avgWeight: 0,
                goodHealthCount: 0,
                petsCount: 0
            },
            
            // 选项数据
            conditionOptions: [
                { value: 1, label: '优秀' },
                { value: 2, label: '良好' },
                { value: 3, label: '一般' },
                { value: 4, label: '差' }
            ],
            appetiteOptions: [
                { value: 1, label: '很好' },
                { value: 2, label: '正常' },
                { value: 3, label: '较差' }
            ],
            energyOptions: [
                { value: 1, label: '活跃' },
                { value: 2, label: '正常' },
                { value: 3, label: '懒散' }
            ]
        }
    },
    created() {
        console.log('🔄 PetHealth组件创建');
        this.initData();
    },
    methods: {
        // ==================== 初始化 ====================
        async initData() {
            console.log('🚀 开始初始化数据...');
            
            // 先获取当前用户信息
            await this.getCurrentUser();
            
            // 获取用户宠物
            await this.fetchMyPets();
            
            // 获取健康记录
            await this.fetchHealthRecords();
        },
        
        // 获取当前用户信息
        async getCurrentUser() {
            try {
                const response = await this.$axios.get('/user/auth');
                console.log('👤 当前用户信息:', response);
                if (response.code === 200) {
                    console.log(`✅ 当前用户ID: ${response.data.id}, 用户名: ${response.data.username}`);
                }
            } catch (error) {
                console.error('获取用户信息失败:', error);
            }
        },
        
        // ==================== 工具方法 ====================
        formatDate(date) {
            if (!date) return '';
            try {
                // 如果已经是 yyyy-MM-dd 格式，直接返回
                if (typeof date === 'string' && date.match(/^\d{4}-\d{2}-\d{2}$/)) {
                    return date;
                }
                
                const d = new Date(date);
                if (isNaN(d.getTime())) {
                    console.warn('无效的日期:', date);
                    return '';
                }
                
                const year = d.getFullYear();
                const month = String(d.getMonth() + 1).padStart(2, '0');
                const day = String(d.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            } catch (error) {
                console.error('日期格式化错误:', error, '原始日期:', date);
                return '';
            }
        },
        
        formatDateTime(date) {
            if (!date) return '';
            try {
                const d = new Date(date);
                if (isNaN(d.getTime())) {
                    return '';
                }
                
                const year = d.getFullYear();
                const month = String(d.getMonth() + 1).padStart(2, '0');
                const day = String(d.getDate()).padStart(2, '0');
                const hours = String(d.getHours()).padStart(2, '0');
                const minutes = String(d.getMinutes()).padStart(2, '0');
                const seconds = String(d.getSeconds()).padStart(2, '0');
                return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
            } catch (error) {
                console.error('日期时间格式化错误:', error);
                return '';
            }
        },
        
        
        // ==================== 图片处理 ====================
        getDefaultPetImage() {
            return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CiAgPGNpcmNsZSBjeD0iNTAiIGN5PSI1MCIgcj0iNDUiIGZpbGw9IiNFNkU2RTYiLz4KICA8cGF0aCBkPSJNNjUgNjBDNjUgNjYuNTcgNjAuNTcgNzEgNTQgNzFIMzZDMjkuNDMgNzEgMjUgNjYuNTcgMjUgNjBWNTFDMjUgNDQuNDMgMjkuNDMgNDAgMzYgNDBINTRDNjAuNTcgNDAgNjUgNDQuNDMgNjUgNTFWNjBaIiBmaWxsPSIjRkZGRkZGIi8+CiAgPGNpcmNsZSBjeD0iNDAiIGN5PSI1MSIgcj0iMyIgZmlsbD0iIzY5NjA1RCIvPgogIDxjaXJjbGUgY3g9IjYwIiBjeT0iNTEiIHI9IjMiIGZpbGw9IiM2OTYwNUQiLz4KPC9zdmc+';
        },
        
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
        
        handleImageError(event) {
            console.log('图片加载失败，使用默认图片');
            event.target.src = this.getDefaultPetImage();
            event.target.onerror = null;
        },
        
        getPetName(petId) {
            const pet = this.myPets.find(p => p.id == petId);
            return pet ? pet.name : '未知宠物';
        },
        
        getPetCover(petId) {
            const pet = this.myPets.find(p => p.id == petId);
            return pet ? this.getSafeImageUrl(pet.cover) : this.getDefaultPetImage();
        },
        
        async fetchMyPets() {
            try {
                console.log('🔄 开始获取用户已领养的宠物...');
                
                // 清空当前列表
                this.myPets = [];
                this.statData.petsCount = 0;
                
                // 只获取已领养的宠物（状态为4：已完成）
                const adoptedPets = await this.getAdoptedPetsOnly();
                
                if (adoptedPets && adoptedPets.length > 0) {
                    console.log(`✅ 成功获取 ${adoptedPets.length} 只已领养宠物`);
                    this.myPets = adoptedPets;
                    this.statData.petsCount = adoptedPets.length;
                    
                    // 设置默认宠物和图表宠物
                    if (this.myPets.length > 0) {
                        this.chartPetId = this.myPets[0].id;
                        this.queryDto.petId = this.myPets[0].id;
                    }
                    
                    return;
                }
                
                // 如果没有领养宠物
                this.showNoPetsMessage();
                
            } catch (error) {
                console.error('❌ 获取宠物信息失败:', error);
                this.showEmptyState();
            }
        },

        // 专门获取已领养宠物的方法（只显示状态为4的）
        async getAdoptedPetsOnly() {
            try {
                console.log('📡 获取已领养宠物（状态4：已完成）...');
                
                // 只查询状态为4的订单（已完成/已领养）
                const response = await this.$axios.post('/pet-adopt-order/queryUser', {
                    status: 4, // 关键：只查询已完成的领养订单
                    current: 1,
                    size: 100
                });
                
                console.log('📦 订单接口响应:', response);
                
                if (response.code === 200) {
                    let data = response.data;
                    let orders = [];
                    
                    // 处理不同格式的响应
                    if (data && data.records) {
                        orders = data.records;
                    } else if (Array.isArray(data)) {
                        orders = data;
                    }
                    
                    console.log('📋 原始订单数量:', orders.length);
                    
                    // 双重验证：前端也过滤一遍状态为4的订单
                    const adoptedOrders = orders.filter(order => {
                        const status = order.status;
                        // 确保状态为4（已完成）
                        return status == 4 || status === '4' || status === 4.0;
                    });
                    
                    console.log('✅ 已领养订单数量:', adoptedOrders.length);
                    
                    if (adoptedOrders.length > 0) {
                        // 去除重复的宠物（同一个宠物可能有多个订单）
                        const uniquePets = [];
                        const petIdSet = new Set();
                        
                        adoptedOrders.forEach(order => {
                            if (order.petId && !petIdSet.has(order.petId)) {
                                petIdSet.add(order.petId);
                                uniquePets.push({
                                    id: order.petId,
                                    name: order.petName || `宠物${order.petId}`,
                                    cover: this.getSafeImageUrl(order.cover),
                                    orderId: order.id,
                                    isAdopted: true,
                                    orderStatus: order.status
                                });
                            }
                        });
                        
                        console.log('🦄 去重后宠物数量:', uniquePets.length);
                        return uniquePets;
                    }
                } else {
                    console.warn('订单接口返回非200状态:', response);
                }
                
                return [];
                
            } catch (error) {
                console.error('获取领养订单失败:', error);
                // 如果接口调用失败，显示友好提示
                this.$message.warning('获取宠物信息失败，请稍后重试');
                return [];
            }
        },

        // 显示无宠物提示
        showNoPetsMessage() {
            this.myPets = [];
            this.statData.petsCount = 0;
            this.healthRecords = [];
            this.total = 0;
            
            // 使用更友好的提示
            this.$notify({
                title: '暂无领养宠物',
                message: '您还没有领养任何宠物，请先前往宠物大厅领养宠物后再使用健康档案功能。',
                type: 'info',
                duration: 6000,
                position: 'bottom-right'
            });
        },
        
        // 方法1: 从订单获取
        async getPetsFromOrders() {
            console.log('📡 方法1: 从订单接口获取');
            
            const response = await this.$axios.post('/pet-adopt-order/queryUser', {
                current: 1,
                size: 100
            });
            
            console.log('📦 订单接口响应:', response);
            
            if (response.code === 200) {
                // 处理分页数据格式
                let data = response.data;
                let orders = [];
                
                if (data && data.records) {
                    // 分页格式：{records: [], total: xx}
                    orders = data.records;
                    console.log('📋 分页格式数据，records数量:', orders.length);
                } else if (Array.isArray(data)) {
                    // 数组格式
                    orders = data;
                    console.log('📋 数组格式数据，数量:', orders.length);
                } else if (data && Array.isArray(data.data)) {
                    // 嵌套格式
                    orders = data.data;
                    console.log('📋 嵌套格式数据，数量:', orders.length);
                }
                
                // 筛选已完成订单(status=4)
                const completedOrders = orders.filter(order => {
                    const status = order.status;
                    console.log('订单状态检查:', order.id, '状态值:', status, '类型:', typeof status);
                    return status == 4 || status === '4';
                });
                
                console.log('✅ 已完成订单数量:', completedOrders.length);
                
                if (completedOrders.length > 0) {
                    return completedOrders.map(order => ({
                        id: order.petId,
                        name: order.petName || `宠物${order.petId}`,
                        cover: this.getSafeImageUrl(order.cover),
                        orderId: order.id,
                        isReal: true
                    }));
                }
            }
            
            return [];
        },
        
        // 方法2: 从宠物列表获取
        async getPetsFromPetList() {
            console.log('📡 方法2: 从宠物列表接口获取');
            
            try {
                // 尝试调用宠物列表接口
                const response = await this.$axios.post('/pet/list', {
                    current: 1,
                    size: 100,
                    isAdopt: 0 // 未领养的
                });
                
                console.log('📦 宠物列表接口响应:', response);
                
                if (response.code === 200) {
                    let data = response.data;
                    let pets = [];
                    
                    if (data && data.records) {
                        pets = data.records;
                    } else if (Array.isArray(data)) {
                        pets = data;
                    } else if (data && Array.isArray(data.data)) {
                        pets = data.data;
                    }
                    
                    if (pets.length > 0) {
                        return pets.map(pet => ({
                            id: pet.id,
                            name: pet.name || `宠物${pet.id}`,
                            cover: this.getSafeImageUrl(pet.cover),
                            isReal: true
                        }));
                    }
                }
            } catch (error) {
                console.log('宠物列表接口失败，尝试其他接口:', error.message);
                
                // 尝试其他可能的接口
                const otherApis = [
                    '/pet/listByUser',
                    '/pet/queryUserPets',
                    '/pet/myPets'
                ];
                
                for (const api of otherApis) {
                    try {
                        const res = await this.$axios.post(api, {});
                        if (res.code === 200 && res.data) {
                            let data = res.data;
                            let pets = [];
                            
                            if (data && data.records) {
                                pets = data.records;
                            } else if (Array.isArray(data)) {
                                pets = data;
                            }
                            
                            if (pets.length > 0) {
                                return pets.map(pet => ({
                                    id: pet.id,
                                    name: pet.name || pet.petName || `宠物${pet.id}`,
                                    cover: this.getSafeImageUrl(pet.cover || pet.avatar),
                                    isReal: true
                                }));
                            }
                        }
                    } catch (err) {
                        console.log(`接口 ${api} 失败:`, err.message);
                    }
                }
            }
            
            return [];
        },
        
        // 方法3: 直接查询数据库获取
        async getPetsFromDirectQuery() {
            console.log('📡 方法3: 直接查询获取');
            
            try {
                // 获取当前用户ID
                const userRes = await this.$axios.get('/user/current');
                if (userRes.code === 200) {
                    const userId = userRes.data.id;
                    
                    // 直接查询该用户的已完成订单
                    const orderRes = await this.$axios.post('/pet-adopt-order/query', {
                        userId: userId,
                        status: 4,
                        current: 1,
                        size: 100
                    });
                    
                    if (orderRes.code === 200) {
                        let data = orderRes.data;
                        let orders = [];
                        
                        if (data && data.records) {
                            orders = data.records;
                        } else if (Array.isArray(data)) {
                            orders = data;
                        }
                        
                        if (orders.length > 0) {
                            return orders.map(order => ({
                                id: order.petId,
                                name: order.petName || `宠物${order.petId}`,
                                cover: this.getSafeImageUrl(order.cover),
                                orderId: order.id,
                                isReal: true
                            }));
                        }
                    }
                }
            } catch (error) {
                console.log('直接查询失败:', error.message);
            }
            
            return [];
        },
        
        // 紧急方案
        useEmergencyPets() {
            console.log('🚨 使用紧急方案宠物数据');
            
            // 根据之前的截图，我们知道有宠物ID为1，用户ID为62
            // 这里直接使用已知数据
            this.myPets = [
                {
                    id: 1,
                    name: '花花',
                    cover: this.getDefaultPetImage(),
                    isReal: true
                }
            ];
            
            this.statData.petsCount = 1;
            this.chartPetId = 1;
            this.queryDto.petId = 1;
            
            console.log('✅ 紧急方案设置完成:', this.myPets);
        },
        
        // 显示空状态
        showEmptyState() {
            this.myPets = [];
            this.statData.petsCount = 0;
            this.healthRecords = [];
            this.total = 0;
            this.weightChartData = [];
            this.weightXAxis = [];
            this.healthDistribution = [];
            this.updateStatistics();
        },
        
        // ==================== 健康记录操作 ====================
        async fetchHealthRecords() {
            try {
                console.log('📋 获取健康记录，参数:', this.queryDto);
                
                // 如果没有宠物，直接返回空数据
                if (this.myPets.length === 0) {
                    console.log('ℹ️ 没有宠物，清空健康记录');
                    this.clearHealthData();
                    this.$message.info('请先领养宠物再查看健康记录');
                    return;
                }
                
                // 如果有宠物但没有选择特定宠物，默认选择第一个
                if (!this.queryDto.petId && this.myPets.length > 0) {
                    this.queryDto.petId = this.myPets[0].id;
                }
                
                console.log('🚀 调用健康记录接口，宠物ID:', this.queryDto.petId);
                
                try {
                    const response = await this.$axios.post('/pet-health-record/list', this.queryDto);
                    console.log('📦 健康记录接口响应:', response);
                    
                    if (response.code === 200) {
                        this.processHealthRecords(response);
                    } else {
                        console.log('❌ 健康记录接口返回错误:', response);
                        this.clearHealthData();
                    }
                } catch (error) {
                    console.error('❌ 健康记录接口调用失败:', error);
                    this.clearHealthData();
                }
                
            } catch (error) {
                console.error('❌ 获取健康记录异常:', error);
                this.clearHealthData();
            }
        },
        
        processHealthRecords(response) {
            const data = response.data;
            let records = [];
            let totalCount = 0;
            
            // 处理分页数据格式
            if (data && data.records) {
                // 分页格式：{records: [], total: xx}
                records = data.records || [];
                totalCount = data.total || records.length;
                console.log('📋 分页数据格式，records:', records.length, 'total:', totalCount);
            } else if (Array.isArray(data)) {
                // 数组格式
                records = data;
                totalCount = data.length;
                console.log('📋 数组数据格式，数量:', records.length);
            } else if (data && Array.isArray(data.data)) {
                // 嵌套格式：{data: []}
                records = data.data || [];
                totalCount = data.total || records.length;
                console.log('📋 嵌套数据格式，数量:', records.length);
            } else {
                // 其他格式
                records = [];
                totalCount = 0;
                console.log('📋 未知数据格式:', data);
            }
            
            this.healthRecords = records;
            this.total = totalCount;
            
            console.log('✅ 获取到健康记录:', this.healthRecords.length, '条', 'total:', this.total);
            
            if (this.healthRecords.length === 0) {
                console.log('ℹ️ 该宠物暂无健康记录');
            } else {
                // 更新图表和统计
                this.updateWeightChart();
                this.updateHealthDistribution();
                this.updateStatistics();
            }
        },
        
        clearHealthData() {
            this.healthRecords = [];
            this.total = 0;
            this.weightChartData = [];
            this.weightXAxis = [];
            this.healthDistribution = [];
            this.updateStatistics();
        },
        
        updateWeightChart() {
            let records = [...this.healthRecords];
            
            // 如果选择了特定宠物，过滤该宠物的记录
            if (this.chartPetId) {
                records = records.filter(record => record.petId === this.chartPetId);
            }
            
            // 过滤有体重的记录
            records = records.filter(record => record.weight);
            
            if (records.length === 0) {
                this.weightChartData = [];
                this.weightXAxis = [];
                return;
            }
            
            // 按日期排序
            records.sort((a, b) => new Date(a.recordDate) - new Date(b.recordDate));
            
            this.weightXAxis = records.map(record => this.formatDate(record.recordDate));
            this.weightChartData = records.map(record => parseFloat(record.weight) || 0);
            
            console.log('📊 体重图表数据:', {
                xAxis: this.weightXAxis,
                data: this.weightChartData
            });
        },
        
        updateHealthDistribution() {
            const distribution = {};
            
            this.healthRecords.forEach(record => {
                const condition = this.getConditionText(record.bodyCondition);
                distribution[condition] = (distribution[condition] || 0) + 1;
            });
            
            this.healthDistribution = Object.entries(distribution).map(([name, value]) => ({
                name,
                value
            }));
            
            console.log('📊 健康状态分布:', this.healthDistribution);
        },
        
        updateStatistics() {
            if (this.healthRecords.length === 0) {
                this.statData = {
                    totalRecords: 0,
                    avgWeight: 0,
                    goodHealthCount: 0,
                    petsCount: this.myPets.length
                };
                return;
            }
            
            const weights = this.healthRecords
                .filter(record => record.weight && !isNaN(parseFloat(record.weight)))
                .map(record => parseFloat(record.weight));
            
            const goodHealthCount = this.healthRecords
                .filter(record => record.bodyCondition && record.bodyCondition <= 2)
                .length;
            
            this.statData = {
                totalRecords: this.total,
                avgWeight: weights.length > 0 ? 
                    weights.reduce((a, b) => a + b, 0) / weights.length : 0,
                goodHealthCount,
                petsCount: this.myPets.length
            };
            
            console.log('📈 统计数据:', this.statData, '分页总数:', this.total);
        },
        
        // ==================== 健康状态相关 ====================
        getConditionText(code) {
            const map = { 1: '优秀', 2: '良好', 3: '一般', 4: '差' };
            return map[code] || '未知';
        },
        
        getConditionTagType(code) {
            const map = { 1: 'success', 2: 'success', 3: 'warning', 4: 'danger' };
            return map[code] || 'info';
        },
        
        getAppetiteText(code) {
            const map = { 1: '很好', 2: '正常', 3: '较差' };
            return map[code] || '未知';
        },
        
        getAppetiteTagType(code) {
            const map = { 1: 'success', 2: 'info', 3: 'warning' };
            return map[code] || 'info';
        },
        
        getEnergyText(code) {
            const map = { 1: '活跃', 2: '正常', 3: '懒散' };
            return map[code] || '未知';
        },
        
        getEnergyTagType(code) {
            const map = { 1: 'success', 2: 'info', 3: 'warning' };
            return map[code] || 'info';
        },
        
        // ==================== 筛选处理 ====================
        handleFilterChange() {
            this.queryDto.current = 1;
            this.fetchHealthRecords();
        },
        
        handleDateChange(dates) {
            if (dates && dates.length === 2) {
                this.queryDto.startDate = dates[0];
                this.queryDto.endDate = dates[1];
            } else {
                this.queryDto.startDate = '';
                this.queryDto.endDate = '';
            }
            this.fetchHealthRecords();
        },
        
        resetFilter() {
            this.queryDto = {
                petId: this.myPets.length > 0 ? this.myPets[0].id : null,
                bodyCondition: null,
                appetite: null,
                startDate: '',
                endDate: '',
                current: 1,
                size: 10
            };
            this.dateRange = [];
            this.fetchHealthRecords();
        },
        
        resetForm() {
            console.log('🔄 重置表单');
            
            // 重置表单数据
            this.formData = {
                id: null,
                petId: this.myPets.length > 0 ? this.myPets[0].id : null,
                recordDate: new Date().toISOString().split('T')[0],
                weight: null,
                height: null,
                bodyCondition: null,
                appetite: null,
                energyLevel: null,
                notes: ''
            };
            
            // 清除表单验证错误
            if (this.$refs.healthForm) {
                this.$refs.healthForm.clearValidate();
            }
            
            // 重置提交状态
            this.submitLoading = false;
            
            console.log('✅ 表单已重置');
        },

        showAddDialog() {
            if (this.myPets.length === 0) {
                this.$message.warning({
                    message: '请先领养宠物再添加健康记录',
                    duration: 3000,
                    showClose: true
                });
                return;
            }
            
            this.dialogTitle = '添加健康记录';
            
            // 先重置表单
            this.resetForm();
            
            // 确保选择第一个宠物
            if (this.myPets.length > 0 && !this.formData.petId) {
                this.formData.petId = this.myPets[0].id;
            }
            
            this.dialogVisible = true;
        }, 
        async deleteRecord(id) {
                    try {
                        await this.$confirm('确定删除这条健康记录吗？删除后无法恢复', '警告', {
                            confirmButtonText: '确定删除',
                            cancelButtonText: '取消',
                            type: 'warning'
                        });
                        
                        const response = await this.$axios.delete(`/pet-health-record/${id}`);
                        if (response.code === 200) {
                            this.$message.success('删除成功');
                            await this.fetchHealthRecords();
                        } else {
                            this.$message.error('删除失败: ' + (response.message || '操作失败'));
                        }
                        
                    } catch (error) {
                        if (error !== 'cancel') {
                            console.error('删除失败:', error);
                            this.$message.error('删除失败: ' + (error.message || '网络错误'));
                        }
                    }
                },        
                
        submitForm() {
            // 确保表单引用存在
            if (!this.$refs.healthForm) {
                console.error('表单引用不存在');
                this.$message.error('表单初始化失败，请刷新页面重试');
                return;
            }
            
            this.$refs.healthForm.validate(async (valid) => {
                if (!valid) return;
                
                this.submitLoading = true;
                try {
                    console.log('💾 提交表单数据:', this.formData);
                    
                    let response;
                    if (this.formData.id) {
                        // 更新
                        response = await this.$axios.put('/pet-health-record/update', this.formData);
                    } else {
                        // 新增
                        const submitData = {
                            ...this.formData,
                            weight: this.formData.weight ? parseFloat(this.formData.weight) : null,
                            height: this.formData.height ? parseFloat(this.formData.height) : null,
                            bodyCondition: this.formData.bodyCondition ? parseInt(this.formData.bodyCondition) : null,
                            appetite: this.formData.appetite ? parseInt(this.formData.appetite) : null,
                            energyLevel: this.formData.energyLevel ? parseInt(this.formData.energyLevel) : null
                        };
                        console.log('📤 发送保存请求:', submitData);
                        response = await this.$axios.post('/pet-health-record/save', submitData);
                    }
                    
                    console.log('💾 保存响应:', response);
                    
                    if (response.code === 200) {
                        this.$message.success(response.message || '操作成功');
                        this.dialogVisible = false;
                        
                        // 延迟刷新，避免立即调用可能导致的问题
                        setTimeout(() => {
                            this.fetchHealthRecords().catch(error => {
                                console.warn('刷新列表时出现小问题，但保存成功:', error);
                            });
                        }, 300);
                    } else {
                        const errorMsg = response.message || '操作失败';
                        
                        if (errorMsg.includes('未领养') || errorMsg.includes('没有权限')) {
                            this.$message.error(errorMsg);
                            await this.fetchMyPets();
                        } else {
                            this.$message.error(errorMsg);
                        }
                    }
                } catch (error) {
                    console.log("❌ 保存健康记录异常:", error);
                    
                    let errorMsg = '操作失败';
                    if (error.response) {
                        if (error.response.status === 403) {
                            errorMsg = '权限不足，请确认您已领养该宠物';
                        } else if (error.response.status === 500) {
                            errorMsg = '服务器内部错误，请稍后重试';
                        } else if (error.response.data && error.response.data.message) {
                            errorMsg = error.response.data.message;
                        }
                    } else if (error.request) {
                        errorMsg = '网络连接失败，请检查网络';
                    } else {
                        errorMsg = error.message || '未知错误';
                    }
                    
                    this.$message.error(errorMsg);
                } finally {
                    this.submitLoading = false;
                }
            });
        },
        editRecord(record) {
            console.log('✏️ 编辑记录:', record);
            
            this.dialogTitle = '编辑健康记录';
            
            // 复制记录数据到表单
            this.formData = {
                id: record.id,
                petId: record.petId,
                recordDate: record.recordDate ? this.formatDate(record.recordDate) : new Date().toISOString().split('T')[0],
                weight: record.weight ? parseFloat(record.weight) : null,
                height: record.height ? parseFloat(record.height) : null,
                bodyCondition: record.bodyCondition ? parseInt(record.bodyCondition) : null,
                appetite: record.appetite ? parseInt(record.appetite) : null,
                energyLevel: record.energyLevel ? parseInt(record.energyLevel) : null,
                notes: record.notes || ''
            };
            
            // 清除表单验证错误
            if (this.$refs.healthForm) {
                this.$refs.healthForm.clearValidate();
            }
            
            this.dialogVisible = true;
        },
                // ==================== 其他功能 ====================
                handleSizeChange(size) {
                    this.queryDto.size = size;
                    this.fetchHealthRecords();
                },
                
                handleCurrentChange(current) {
                    this.queryDto.current = current;
                    this.fetchHealthRecords();
                }
            }
        }
</script>

<style scoped lang="scss">
.pet-health-container {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 40px);
    
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        
        h1 {
            margin: 0;
            color: #303133;
            font-size: 24px;
            font-weight: 600;
        }
        
        .header-actions {
            display: flex;
            gap: 10px;
        }
    }
    
    .stat-cards {
        margin-bottom: 20px;
        
        .stat-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.05);
            display: flex;
            align-items: center;
            transition: transform 0.3s ease;
            
            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            }
            
            .stat-icon {
                width: 50px;
                height: 50px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 15px;
                
                i {
                    font-size: 24px;
                    color: white;
                }
            }
            
            .stat-content {
                .stat-value {
                    font-size: 24px;
                    font-weight: bold;
                    color: #303133;
                    line-height: 1;
                }
                
                .stat-label {
                    font-size: 14px;
                    color: #909399;
                    margin-top: 5px;
                }
            }
        }
    }
    
    .filter-section {
        margin-bottom: 20px;
        
        .filter-form {
            margin-bottom: 0;
            
            .el-form-item {
                margin-bottom: 0;
            }
        }
    }
    
    .chart-section {
        margin-bottom: 20px;
        
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-weight: 600;
            
            span {
                font-size: 16px;
                color: #303133;
            }
        }
        
        .empty-chart {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 300px;
            background: #f8f9fa;
            border-radius: 8px;
        }
    }
    
    .record-section {
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-weight: 600;
            
            span {
                font-size: 16px;
                color: #303133;
            }
        }
        
        .empty-state {
            padding: 60px 0;
        }
        
        .record-list {
            .record-card {
                background: white;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 15px;
                border: 1px solid #ebeef5;
                transition: all 0.3s ease;
                
                &:hover {
                    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
                    border-color: #409eff;
                }
                
                .record-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: flex-start;
                    margin-bottom: 15px;
                    padding-bottom: 15px;
                    border-bottom: 1px solid #ebeef5;
                    
                    .pet-info {
                        display: flex;
                        align-items: flex-start;
                        
                        .pet-avatar {
                            width: 50px;
                            height: 50px;
                            border-radius: 50%;
                            object-fit: cover;
                            margin-right: 15px;
                            border: 2px solid #f0f0f0;
                            background-color: #f8f9fa;
                        }
                        
                        .pet-details {
                            .pet-name {
                                font-size: 16px;
                                font-weight: 600;
                                color: #303133;
                                margin-bottom: 8px;
                            }
                            
                            .record-meta {
                                display: flex;
                                gap: 8px;
                            }
                        }
                    }
                    
                    .record-actions {
                        display: flex;
                        gap: 8px;
                    }
                }
                
                .record-content {
                    margin-bottom: 15px;
                    
                    .health-indicator {
                        padding: 10px;
                        background: #f8f9fa;
                        border-radius: 6px;
                        height: 100%;
                        
                        .indicator-label {
                            font-size: 12px;
                            color: #909399;
                            margin-bottom: 5px;
                        }
                        
                        .indicator-value {
                            .value-number {
                                font-size: 18px;
                                font-weight: bold;
                                color: #303133;
                            }
                            
                            .value-empty {
                                font-size: 14px;
                                color: #c0c4cc;
                                font-style: italic;
                            }
                        }
                    }
                    
                    .record-notes {
                        margin-top: 15px;
                        padding: 15px;
                        background: #f0f9ff;
                        border-radius: 6px;
                        border-left: 4px solid #409eff;
                        
                        .notes-label {
                            font-size: 14px;
                            font-weight: 500;
                            color: #409eff;
                            margin-bottom: 8px;
                            
                            i {
                                margin-right: 5px;
                            }
                        }
                        
                        .notes-content {
                            font-size: 14px;
                            color: #606266;
                            line-height: 1.6;
                        }
                    }
                }
                
                .record-footer {
                    display: flex;
                    justify-content: flex-end;
                    align-items: center;
                    padding-top: 15px;
                    border-top: 1px solid #ebeef5;
                    font-size: 12px;
                    color: #909399;
                    
                    i {
                        margin-right: 5px;
                    }
                }
            }
        }
        
        .pagination-wrapper {
            margin-top: 20px;
            display: flex;
            justify-content: center;
        }
    }
}

// 响应式设计
@media (max-width: 1200px) {
    .pet-health-container {
        .record-content {
            .el-col {
                margin-bottom: 15px;
            }
        }
    }
}

@media (max-width: 768px) {
    .pet-health-container {
        padding: 15px;
        
        .header {
            flex-direction: column;
            align-items: flex-start;
            gap: 15px;
            margin-bottom: 15px;
            
            h1 {
                font-size: 20px;
            }
        }
        
        .record-header {
            flex-direction: column;
            gap: 15px;
            
            .record-actions {
                align-self: flex-start;
            }
        }
    }
}
</style>