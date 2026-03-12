<template>
    <div class="pet-health-manage">
        <!-- 页面标题 -->
        <div class="header">
            <h1>📋 宠物健康档案管理</h1>
            <p class="sub-title">管理系统所有宠物的健康记录数据</p>
        </div>

        <!-- 筛选和统计区域 -->
        <div class="filter-section">
            <el-card shadow="never">
                <el-form :inline="true" :model="queryDto" class="filter-form">
                    <el-form-item label="宠物名称">
                        <el-input 
                            v-model="queryDto.petName" 
                            placeholder="输入宠物名称" 
                            clearable
                            @keyup.enter="fetchHealthRecords"
                            style="width: 180px;">
                            <i slot="prefix" class="el-input__icon el-icon-search"></i>
                        </el-input>
                    </el-form-item>
                    
                    <el-form-item label="记录日期">
                        <el-date-picker
                            v-model="dateRange"
                            type="daterange"
                            range-separator="至"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            value-format="yyyy-MM-dd"
                            @change="handleDateChange"
                            style="width: 250px;">
                        </el-date-picker>
                    </el-form-item>
                    
                    <el-form-item label="身体状况">
                        <el-select v-model="queryDto.bodyCondition" placeholder="身体状况" clearable style="width: 120px;">
                            <el-option v-for="item in conditionOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    
                    <el-form-item>
                        <el-button type="primary" @click="fetchHealthRecords" icon="el-icon-search">查询</el-button>
                        <el-button @click="resetFilter" icon="el-icon-refresh">重置</el-button>
                    </el-form-item>
                </el-form>
                
                <!-- 快速统计 -->
                <div class="quick-stats">
                    <el-row :gutter="20">
                        <el-col :span="6">
                            <div class="stat-item">
                                <div class="stat-label">总记录数</div>
                                <div class="stat-value">{{ statData.totalRecords }}</div>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="stat-item">
                                <div class="stat-label">涉及宠物数</div>
                                <div class="stat-value">{{ statData.petsCount }}</div>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="stat-item">
                                <div class="stat-label">平均记录数</div>
                                <div class="stat-value">{{ statData.avgPerPet }}</div>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div class="stat-item">
                                <div class="stat-label">最新记录</div>
                                <div class="stat-value">{{ statData.latestDate || '无' }}</div>
                            </div>
                        </el-col>
                    </el-row>
                </div>
            </el-card>
        </div>

        <!-- 数据表格区域 -->
        <div class="table-section">
            <el-card shadow="never">
                <template #header>
                    <div class="card-header">
                        <span>健康记录列表</span>
                        <el-tag type="info">共 {{ total }} 条记录</el-tag>
                    </div>
                </template>
                
                <!-- 空状态 -->
                <div v-if="healthRecords.length === 0" class="empty-state">
                    <el-empty description="暂无健康记录数据"></el-empty>
                </div>
                
                <!-- 数据表格 -->
                <el-table 
                    v-else
                    :data="healthRecords" 
                    style="width: 100%"
                    stripe
                    border
                    v-loading="loading"
                    element-loading-text="数据加载中...">
                    
                    <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
                    
                    <el-table-column label="宠物信息" width="180">
                        <template slot-scope="scope">
                            <div class="pet-info-cell">
                                <img :src="getSafePetCover(scope.row)" class="pet-avatar" alt="宠物头像" @error="handleImageError">
                                <div class="pet-details">
                                    <div class="pet-name">{{ getSafePetName(scope.row) }}</div>
                                    <div class="pet-id">ID: {{ scope.row.petId || '未知' }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    
                    <el-table-column prop="recordDate" label="记录日期" width="120" align="center">
                        <template slot-scope="scope">
                            {{ formatDate(scope.row.recordDate) }}
                        </template>
                    </el-table-column>
                    
                    <el-table-column label="体重/身高" width="140" align="center">
                        <template slot-scope="scope">
                            <div v-if="scope.row.weight">
                                <el-tag size="mini" type="info">{{ scope.row.weight }}kg</el-tag>
                            </div>
                            <div v-else class="empty-field">未记录</div>
                            <div v-if="scope.row.height">
                                <el-tag size="mini" type="info" style="margin-top: 4px;">{{ scope.row.height }}cm</el-tag>
                            </div>
                        </template>
                    </el-table-column>
                    
                    <el-table-column prop="bodyCondition" label="身体状况" width="120" align="center">
                        <template slot-scope="scope">
                            <el-tag 
                                size="medium" 
                                :type="getConditionTagType(scope.row.bodyCondition)"
                                effect="dark">
                                {{ getConditionText(scope.row.bodyCondition) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    
                    <el-table-column prop="appetite" label="食欲情况" width="100" align="center">
                        <template slot-scope="scope">
                            <el-tag 
                                size="small" 
                                :type="getAppetiteTagType(scope.row.appetite)">
                                {{ getAppetiteText(scope.row.appetite) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    
                    <el-table-column prop="energyLevel" label="活力水平" width="100" align="center">
                        <template slot-scope="scope">
                            <el-tag 
                                size="small" 
                                :type="getEnergyTagType(scope.row.energyLevel)">
                                {{ getEnergyText(scope.row.energyLevel) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    
                    <el-table-column prop="notes" label="备注" min-width="150" show-overflow-tooltip>
                        <template slot-scope="scope">
                            <div class="notes-content">
                                {{ scope.row.notes || '无备注' }}
                            </div>
                        </template>
                    </el-table-column>
                    
                    <!-- 添加领养人列 -->
                    <el-table-column label="领养人" width="150">
                        <template slot-scope="scope">
                            <div class="owner-info-cell">
                                <img :src="getSafeOwnerAvatar(scope.row)" 
                                    class="owner-avatar" 
                                    alt="领养人头像"
                                    @error="handleImageError">
                                <div class="owner-details">
                                    <div class="owner-name">{{ getSafeOwnerName(scope.row) }}</div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="创建时间" width="160" align="center">
                        <template slot-scope="scope">
                            {{ formatDateTime(scope.row.createTime) }}
                        </template>
                    </el-table-column>
                    
                    <el-table-column label="操作" width="210" fixed="right" align="center">
                        <template slot-scope="scope">
                            <el-button 
                                size="mini" 
                                type="primary" 
                                icon="el-icon-view"
                                @click="viewDetail(scope.row)">
                                详情
                            </el-button>
                            <el-button 
                                size="mini" 
                                type="success" 
                                icon="el-icon-pie-chart"
                                @click="showPetCharts(scope.row)">
                                查看图表
                            </el-button>
                            <el-button 
                                size="mini" 
                                type="danger" 
                                icon="el-icon-delete"
                                @click="deleteRecord(scope.row.id)">
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <!-- 分页 -->
                <div class="pagination-wrapper" v-if="healthRecords.length > 0">
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

        <!-- 详情对话框 -->
        <el-dialog 
            title="健康记录详情" 
            :visible.sync="detailDialogVisible" 
            width="600px"
            @close="closeDetailDialog">
            
            <el-card shadow="never" v-if="currentRecord">
                <div class="detail-content">
                    <!-- 宠物信息 -->
                    <div class="section">
                        <h3 class="section-title">📌 宠物信息</h3>
                        <el-descriptions :column="2" border size="medium">
                            <el-descriptions-item label="宠物名称">
                                <div class="pet-info-detail">
                                    <img :src="getSafePetCover(currentRecord)" class="detail-pet-avatar" alt="宠物头像" @error="handleImageError">
                                    <span>{{ getSafePetName(currentRecord) }}</span>
                                </div>
                            </el-descriptions-item>
                            <el-descriptions-item label="宠物ID">{{ currentRecord.petId }}</el-descriptions-item>
                        </el-descriptions>
                    </div>

                    <!-- 添加领养人信息 -->
                    <div class="section">
                        <h3 class="section-title">👤 领养人信息</h3>
                        <el-descriptions :column="1" border size="medium">
                            <el-descriptions-item label="领养人">
                                <div class="owner-info-detail">
                                    <img :src="getSafeOwnerAvatar(currentRecord)" 
                                        class="detail-owner-avatar" 
                                        alt="领养人头像"
                                        @error="handleImageError">
                                    <span>{{ getSafeOwnerName(currentRecord) }}</span>
                                </div>
                            </el-descriptions-item>
                        </el-descriptions>
                    </div>
                    
                    <!-- 健康信息 -->
                    <div class="section">
                        <h3 class="section-title">🏥 健康信息</h3>
                        <el-descriptions :column="2" border size="medium">
                            <el-descriptions-item label="记录日期">
                                <el-tag type="info">{{ formatDate(currentRecord.recordDate) }}</el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="体重">
                                <span v-if="currentRecord.weight" class="value-highlight">{{ currentRecord.weight }} kg</span>
                                <span v-else class="value-empty">未记录</span>
                            </el-descriptions-item>
                            <el-descriptions-item label="身高">
                                <span v-if="currentRecord.height" class="value-highlight">{{ currentRecord.height }} cm</span>
                                <span v-else class="value-empty">未记录</span>
                            </el-descriptions-item>
                            <el-descriptions-item label="身体状况">
                                <el-tag 
                                    :type="getConditionTagType(currentRecord.bodyCondition)"
                                    size="medium">
                                    {{ getConditionText(currentRecord.bodyCondition) }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="食欲情况">
                                <el-tag 
                                    :type="getAppetiteTagType(currentRecord.appetite)"
                                    size="medium">
                                    {{ getAppetiteText(currentRecord.appetite) }}
                                </el-tag>
                            </el-descriptions-item>
                            <el-descriptions-item label="活力水平">
                                <el-tag 
                                    :type="getEnergyTagType(currentRecord.energyLevel)"
                                    size="medium">
                                    {{ getEnergyText(currentRecord.energyLevel) }}
                                </el-tag>
                            </el-descriptions-item>
                        </el-descriptions>
                    </div>
                    
                    <!-- 备注信息 -->
                    <div class="section" v-if="currentRecord.notes">
                        <h3 class="section-title">📝 备注信息</h3>
                        <el-card shadow="never" class="notes-card">
                            <div class="notes-text">{{ currentRecord.notes }}</div>
                        </el-card>
                    </div>
                    
                    <!-- 系统信息 -->
                    <div class="section">
                        <h3 class="section-title">⚙️ 系统信息</h3>
                        <el-descriptions :column="1" border size="medium">
                            <el-descriptions-item label="记录ID">{{ currentRecord.id }}</el-descriptions-item>
                            <el-descriptions-item label="创建时间">{{ formatDateTime(currentRecord.createTime) }}</el-descriptions-item>
                        </el-descriptions>
                    </div>
                </div>
            </el-card>
            
            <span slot="footer" class="dialog-footer">
                <el-button @click="detailDialogVisible = false" size="medium">关闭</el-button>
                <el-button type="primary" @click="editRecord(currentRecord)" size="medium" v-if="hasEditPermission">编辑</el-button>
            </span>
        </el-dialog>

        <!-- 新增：宠物健康图表对话框 -->
        <el-dialog 
            :title="`${getSafePetName(selectedPet)} - 健康趋势分析`"  
            :visible.sync="chartDialogVisible" 
            width="800px"
            @close="closeChartDialog"
            :fullscreen="isFullscreen">
            
            <div class="chart-dialog-content">
                <!-- 宠物基本信息 -->
                <el-card shadow="never" class="pet-info-card" v-if="selectedPet">
                    <div class="pet-basic-info">
                        <img :src="getSafePetCover(selectedPet)" 
                            class="chart-pet-avatar" 
                            alt="宠物头像" 
                            @error="handleImageError">
                        <div class="pet-info-text">
                            <h3>{{ getSafePetName(selectedPet) }}</h3>
                            <p class="pet-id">宠物ID: {{ selectedPet.petId || '未知' }}</p>
                            <p class="owner-info">
                                领养人: {{ getSafeOwnerName(selectedPet) }}
                            </p>
                        </div>
                    </div>
                </el-card>

                <!-- 图表区域 -->
                <div class="charts-container" v-loading="chartLoading" element-loading-text="加载图表数据中...">
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-card shadow="never" class="chart-card">
                                <template #header>
                                    <div class="chart-card-header">
                                        <span>📈 体重变化趋势</span>
                                        <el-tag v-if="petWeightChartData.length > 0" size="mini" type="success">
                                            {{ petWeightChartData.length }} 条记录
                                        </el-tag>
                                    </div>
                                </template>
                                <LineChart 
                                    v-if="petWeightChartData.length > 0"
                                    :height="'350px'" 
                                    :values="petWeightChartData" 
                                    :date="petWeightXAxis" 
                                    :tag="'体重变化'"
                                    :subtext="'体重(kg)'"/>
                                <div v-else class="empty-chart">
                                    <el-empty description="该宠物暂无体重记录" :image-size="80"></el-empty>
                                </div>
                            </el-card>
                        </el-col>
                        
                        <el-col :span="12">
                            <el-card shadow="never" class="chart-card">
                                <template #header>
                                    <div class="chart-card-header">
                                        <span>📊 健康状态分布</span>
                                        <el-tag v-if="petHealthDistribution.length > 0" size="mini" type="success">
                                            {{ petTotalRecords }} 条记录
                                        </el-tag>
                                    </div>
                                </template>
                                <PieCharts 
                                    v-if="petHealthDistribution.length > 0"
                                    :height="'350px'" 
                                    :data="petHealthDistribution"/>
                                <div v-else class="empty-chart">
                                    <el-empty description="该宠物暂无健康状态数据" :image-size="80"></el-empty>
                                </div>
                            </el-card>
                        </el-col>
                    </el-row>

                    <!-- 健康记录统计 -->
                    <el-card shadow="never" class="stats-card" v-if="petRecords.length > 0">
                        <template #header>
                            <div class="stats-header">
                                <span>📋 健康记录统计</span>
                            </div>
                        </template>
                        <el-row :gutter="20">
                            <el-col :span="6">
                                <div class="pet-stat-item">
                                    <div class="pet-stat-label">总记录数</div>
                                    <div class="pet-stat-value">{{ petTotalRecords }}</div>
                                </div>
                            </el-col>
                            <el-col :span="6">
                                <div class="pet-stat-item">
                                    <div class="pet-stat-label">平均体重</div>
                                    <div class="pet-stat-value">{{ petAvgWeight }} kg</div>
                                </div>
                            </el-col>
                            <el-col :span="6">
                                <div class="pet-stat-item">
                                    <div class="pet-stat-label">平均身高</div>
                                    <div class="pet-stat-value">{{ petAvgHeight }} cm</div>
                                </div>
                            </el-col>
                            <el-col :span="6">
                                <div class="pet-stat-item">
                                    <div class="pet-stat-label">最新记录</div>
                                    <div class="pet-stat-value">{{ petLatestDate }}</div>
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>
                </div>
            </div>
            
            <span slot="footer" class="dialog-footer">
                <el-button @click="toggleFullscreen" size="medium" icon="el-icon-full-screen">
                    {{ isFullscreen ? '退出全屏' : '全屏' }}
                </el-button>
                <el-button @click="chartDialogVisible = false" size="medium">关闭</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { formatDate, formatDateTime } from '@/utils/date.js';
import LineChart from '@/components/LineChart.vue';
import PieCharts from '@/components/PieCharts.vue';

export default {
    name: 'PetHealthManage',
    components: { LineChart, PieCharts },
    data() {
        return {
            // 查询条件
            queryDto: {
                petName: '',
                startDate: '',
                endDate: '',
                bodyCondition: null,
                current: 1,
                size: 20,
                hasInitialized:false
            },
            dateRange: [],
            
            // 数据
            healthRecords: [],
            total: 0,
            loading: false,
            
            // 详情对话框
            detailDialogVisible: false,
            currentRecord: null,
            
            // 图表对话框相关
            chartDialogVisible: false,
            selectedPet: null,
            petRecords: [], // 单个宠物的所有健康记录
            petWeightChartData: [],
            petWeightXAxis: [],
            petHealthDistribution: [],
            isFullscreen: false,
            chartLoading: false,
            
            // 宠物统计信息
            petTotalRecords: 0,
            petAvgWeight: '0.00',
            petAvgHeight: '0.00',
            petLatestDate: '无',
            
            // 全局统计信息
            statData: {
                totalRecords: 0,
                petsCount: 0,
                avgPerPet: 0,
                latestDate: ''
            },
            
            // 选项数据
            conditionOptions: [
                { value: 1, label: '优秀' },
                { value: 2, label: '良好' },
                { value: 3, label: '一般' },
                { value: 4, label: '差' }
            ],
            
            // 权限控制
            hasEditPermission: false,
            isAdmin: false
        }
    },
    created() {

    console.log('🚀 PetHealthManage组件创建');
    
    // 防止重复调用
    if (this.hasInitialized) {
        console.log('⚠️ 已初始化，跳过');
        return;
    }
    
    this.hasInitialized = true;
    
    // 检查权限后获取数据
    this.checkAdminRole().then(() => {
        console.log('✅ 权限验证通过，获取健康记录');
        this.fetchHealthRecords();
    }).catch(error => {
        console.error('❌ 权限检查失败:', error);
        this.$message.error('权限验证失败: ' + (error.message || '未知错误'));
    });
    },
    methods: {
        formatDate,
        formatDateTime,
        
        // 图片处理方法
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
            event.target.src = this.getDefaultPetImage();
            event.target.onerror = null;
        },
        
        // 安全访问方法
        getSafePetName(record) {
            if (!record) return '未知宠物';
            return record.petName || '未知宠物';
        },
        
        getSafePetCover(record) {
            if (!record) return this.getDefaultPetImage();
            return record.petCover || this.getDefaultPetImage();
        },
        
        getSafeOwnerName(record) {
            if (!record) return '未知用户';
            return record.ownerName || '未知用户';
        },
        
        getSafeOwnerAvatar(record) {
            if (!record) return this.getDefaultPetImage();
            return this.getSafeImageUrl(record.ownerAvatar);
        },
        
        // 检查是否为管理员
        async checkAdminRole() {
            try {
                // 获取当前用户信息
                const response = await this.$axios.get('/user/auth');
                console.log('👮 用户信息响应:', response);
                
                if (response && response.code === 200) {
                    const userData = response.data;
                    this.isAdmin = userData.role === 1; // 假设1是管理员
                    this.hasEditPermission = !this.isAdmin; // 管理员只能查看
                    
                    console.log(`👮 用户角色: ${this.isAdmin ? '管理员' : '普通用户'}`);
                } else {
                    console.error('获取用户信息失败:', response);
                }
            } catch (error) {
                console.error('获取用户信息失败:', error);
            }
        },

        // ✅ 优化后的数据获取方法
        async fetchHealthRecords() {
                console.log('📤 调用fetchHealthRecords，使用POST方法');
                console.log('请求URL:', '/pet-health-record/list');
                console.log('请求方法:', 'POST');
                console.log('请求参数:', JSON.stringify(this.queryDto));
                this.loading = true;

            try {
                console.log('🔄 管理员查询参数:', this.queryDto);
                
                // 使用POST方法调用接口
                const response = await this.$axios.post('/pet-health-record/list', this.queryDto);
                
                console.log('📦 管理员端响应:', response);
                
                // ✅ 优化响应格式处理
                let responseData = response;
                
                // 如果返回的是标准格式 {code: 200, data: {...}, message: ''}
                if (response && response.code === 200) {
                    responseData = response.data;
                }
                
                // 处理分页格式
                if (responseData && responseData.records !== undefined) {
                    // 格式：{records: [], total: xx}
                    this.healthRecords = responseData.records || [];
                    this.total = responseData.total || 0;
                } else if (Array.isArray(responseData)) {
                    // 格式：[]
                    this.healthRecords = responseData;
                    this.total = responseData.length;
                } else if (responseData) {
                    // 其他格式，尝试直接使用
                    this.healthRecords = responseData;
                    this.total = responseData.length || 0;
                    console.warn('⚠️ 未知的响应格式，尝试处理:', responseData);
                } else {
                    this.healthRecords = [];
                    this.total = 0;
                    console.warn('⚠️ 响应数据为空');
                }
                
                // 更新统计信息
                this.updateStatistics();
                
                console.log(`✅ 获取到 ${this.healthRecords.length} 条记录`);
                
            } catch (error) {
                console.error("❌ 获取健康记录异常:", error);
                
                let errorMsg = '获取数据失败';
                if (error && error.response) {
                    if (error.response.data && error.response.data.message) {
                        errorMsg = error.response.data.message;
                    } else if (error.response.status === 405) {
                        errorMsg = '请求方法错误，请检查接口调用方式';
                    }
                } else if (error && error.message) {
                    errorMsg = error.message;
                }
                
                this.$message.error(errorMsg);
                this.healthRecords = [];
                this.total = 0;
                this.updateStatistics();
            } finally {
                this.loading = false;
            }
        },
        
        // 更新统计信息
        updateStatistics() {
            if (this.healthRecords.length === 0) {
                this.statData = {
                    totalRecords: 0,
                    petsCount: 0,
                    avgPerPet: 0,
                    latestDate: ''
                };
                return;
            }
            
            try {
                const petIds = [...new Set(this.healthRecords.map(record => record.petId))];
                
                const sortedRecords = [...this.healthRecords].sort((a, b) => 
                    new Date(b.recordDate) - new Date(a.recordDate)
                );
                
                this.statData = {
                    totalRecords: this.healthRecords.length,
                    petsCount: petIds.length,
                    avgPerPet: petIds.length > 0 ? (this.healthRecords.length / petIds.length).toFixed(1) : 0,
                    latestDate: sortedRecords[0] ? this.formatDate(sortedRecords[0].recordDate) : ''
                };
            } catch (error) {
                console.error('更新统计信息失败:', error);
                this.statData = {
                    totalRecords: 0,
                    petsCount: 0,
                    avgPerPet: 0,
                    latestDate: ''
                };
            }
        },
        
        // 显示宠物图表
        async showPetCharts(record) {
            this.chartDialogVisible = true;
            this.chartLoading = true;
            
            // 安全设置selectedPet
            this.selectedPet = {
                petName: this.getSafePetName(record),
                petId: record && record.petId ? record.petId : null,
                petCover: this.getSafePetCover(record),
                ownerName: this.getSafeOwnerName(record)
            };
            
            try {
                console.log(`📊 获取宠物 ${record.petId} 的健康记录`);
                
                // 调用后端接口获取单个宠物的所有健康记录
                const response = await this.$axios.get(`/pet-health-record/pet/${record.petId}`);
                console.log(`📦 宠物 ${record.petId} 的响应:`, response);
                
                let petResponseData = response;
                if (response && response.code === 200) {
                    petResponseData = response.data;
                }
                
                // 处理响应数据
                if (Array.isArray(petResponseData)) {
                    this.petRecords = petResponseData;
                } else if (petResponseData && petResponseData.records) {
                    this.petRecords = petResponseData.records;
                } else {
                    this.petRecords = [];
                    console.warn('⚠️ 宠物健康记录响应格式未知:', petResponseData);
                }
                
                // 更新宠物图表
                this.updatePetCharts();
                this.updatePetStatistics();
                
                console.log(`✅ 获取到宠物 ${record.petId} 的 ${this.petRecords.length} 条记录`);
                
            } catch (error) {
                console.error(`❌ 获取宠物 ${record.petId} 健康记录异常:`, error);
                
                let errorMsg = '获取宠物健康记录失败';
                if (error && error.response) {
                    if (error.response.data && error.response.data.message) {
                        errorMsg = error.response.data.message;
                    }
                }
                
                this.$message.error(errorMsg);
                this.petRecords = [];
                this.updatePetCharts();
                this.updatePetStatistics();
            } finally {
                this.chartLoading = false;
            }
        },
        
        // 更新宠物图表
        updatePetCharts() {
            if (this.petRecords.length === 0) {
                this.petWeightChartData = [];
                this.petWeightXAxis = [];
                this.petHealthDistribution = [];
                return;
            }
            
            try {
                // 宠物体重趋势图（按日期排序）
                const sortedRecords = [...this.petRecords].sort((a, b) => 
                    new Date(a.recordDate) - new Date(b.recordDate)
                );
                
                this.petWeightXAxis = sortedRecords.map(record => this.formatDate(record.recordDate));
                this.petWeightChartData = sortedRecords.map(record => parseFloat(record.weight) || 0);
                
                // 宠物健康状态分布
                const distribution = {};
                this.petRecords.forEach(record => {
                    const condition = this.getConditionText(record.bodyCondition);
                    distribution[condition] = (distribution[condition] || 0) + 1;
                });
                
                this.petHealthDistribution = Object.entries(distribution).map(([name, value]) => ({
                    name,
                    value
                }));
                
            } catch (error) {
                console.error('更新宠物图表失败:', error);
                this.petWeightChartData = [];
                this.petWeightXAxis = [];
                this.petHealthDistribution = [];
            }
        },
        
        // 更新宠物统计信息
        updatePetStatistics() {
            if (this.petRecords.length === 0) {
                this.petTotalRecords = 0;
                this.petAvgWeight = '0.00';
                this.petAvgHeight = '0.00';
                this.petLatestDate = '无';
                return;
            }
            
            try {
                // 计算平均体重
                const weightRecords = this.petRecords.filter(record => record.weight);
                const totalWeight = weightRecords.reduce((sum, record) => sum + parseFloat(record.weight), 0);
                this.petAvgWeight = weightRecords.length > 0 ? (totalWeight / weightRecords.length).toFixed(2) : '0.00';
                
                // 计算平均身高
                const heightRecords = this.petRecords.filter(record => record.height);
                const totalHeight = heightRecords.reduce((sum, record) => sum + parseFloat(record.height), 0);
                this.petAvgHeight = heightRecords.length > 0 ? (totalHeight / heightRecords.length).toFixed(2) : '0.00';
                
                // 最新记录日期
                const sortedRecords = [...this.petRecords].sort((a, b) => 
                    new Date(b.recordDate) - new Date(a.recordDate)
                );
                this.petLatestDate = sortedRecords[0] ? this.formatDate(sortedRecords[0].recordDate) : '无';
                
                // 总记录数
                this.petTotalRecords = this.petRecords.length;
                
            } catch (error) {
                console.error('更新宠物统计信息失败:', error);
                this.petTotalRecords = 0;
                this.petAvgWeight = '0.00';
                this.petAvgHeight = '0.00';
                this.petLatestDate = '无';
            }
        },
        
        // 关闭图表对话框
        closeChartDialog() {
            this.selectedPet = null;
            this.petRecords = [];
            this.petWeightChartData = [];
            this.petWeightXAxis = [];
            this.petHealthDistribution = [];
            this.isFullscreen = false;
        },
        
        // 切换全屏
        toggleFullscreen() {
            this.isFullscreen = !this.isFullscreen;
        },
        
        // 健康状态相关方法
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
        
        // 筛选处理
        handleDateChange(dates) {
            if (dates && dates.length === 2) {
                this.queryDto.startDate = dates[0];
                this.queryDto.endDate = dates[1];
            } else {
                this.queryDto.startDate = '';
                this.queryDto.endDate = '';
            }
        },
        
        resetFilter() {
            this.queryDto = {
                petName: '',
                startDate: '',
                endDate: '',
                bodyCondition: null,
                current: 1,
                size: 20
            };
            this.dateRange = [];
            this.fetchHealthRecords();
        },
        
        // 查看详情
        viewDetail(record) {
            this.currentRecord = record;
            this.detailDialogVisible = true;
        },
        
        closeDetailDialog() {
            this.currentRecord = null;
        },
        
        // 编辑记录
        editRecord(record) {
            if (this.isAdmin) {
                this.$message.warning('管理员只能查看健康记录，不能编辑');
                return;
            }
            this.$message.info('编辑功能开发中...');
        },
        
        // 删除记录
        async deleteRecord(id) {
            try {
                await this.$confirm('确定删除这条健康记录吗？此操作不可恢复', '警告', {
                    confirmButtonText: '确定删除',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                const response = await this.$axios.delete(`/pet-health-record/${id}`);
                if (response && response.code === 200) {
                    this.$message.success('删除成功');
                    this.fetchHealthRecords();
                } else {
                    this.$message.error(response ? response.message : '删除失败');
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除记录失败:', error);
                    this.$message.error('删除失败');
                }
            }
        },
        
        // 分页处理
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
.pet-health-manage {
    padding: 20px;
    
    .header {
        margin-bottom: 20px;
        
        h1 {
            margin: 0;
            color: #303133;
            font-size: 24px;
            font-weight: 600;
        }
        
        .sub-title {
            margin: 8px 0 0;
            color: #909399;
            font-size: 14px;
        }
    }
    
    .filter-section {
        margin-bottom: 20px;
        
        .filter-form {
            margin-bottom: 20px;
            
            .el-form-item {
                margin-bottom: 0;
            }
        }
        
        .quick-stats {
            .stat-item {
                padding: 15px;
                background: #f8f9fa;
                border-radius: 8px;
                text-align: center;
                
                .stat-label {
                    font-size: 12px;
                    color: #909399;
                    margin-bottom: 5px;
                }
                
                .stat-value {
                    font-size: 24px;
                    font-weight: bold;
                    color: #303133;
                }
            }
        }
    }
    
    .table-section {
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
        
        .pet-info-cell {
            display: flex;
            align-items: center;
            
            .pet-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
                margin-right: 10px;
                border: 2px solid #f0f0f0;
                background-color: #f8f9fa;
            }
            
            .pet-details {
                .pet-name {
                    font-size: 14px;
                    font-weight: 600;
                    color: #303133;
                    margin-bottom: 4px;
                }
                
                .pet-id {
                    font-size: 12px;
                    color: #909399;
                }
            }
        }
        
        .notes-content {
            max-height: 60px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            display: -moz-box;
            display: flex;
            -webkit-line-clamp: 3;
            -moz-line-clamp: 3;
            line-clamp: 3;
            -webkit-box-orient: vertical;
            -moz-box-orient: vertical;
            box-orient: vertical;
        }
        
        .empty-field {
            color: #c0c4cc;
            font-style: italic;
            font-size: 12px;
        }
        
        .owner-info-cell {
            display: flex;
            align-items: center;
            
            .owner-avatar {
                width: 30px;
                height: 30px;
                border-radius: 50%;
                object-fit: cover;
                margin-right: 10px;
                border: 2px solid #f0f0f0;
                background-color: #f8f9fa;
            }
            
            .owner-details {
                .owner-name {
                    font-size: 14px;
                    color: #303133;
                    font-weight: 500;
                }
            }
        }
        
        .pagination-wrapper {
            margin-top: 20px;
            display: flex;
            justify-content: center;
        }
    }
    
    .detail-content {
        .section {
            margin-bottom: 24px;
            
            &:last-child {
                margin-bottom: 0;
            }
            
            .section-title {
                font-size: 16px;
                font-weight: 600;
                color: #303133;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                
                &::before {
                    content: '';
                    display: inline-block;
                    width: 4px;
                    height: 16px;
                    background: #409eff;
                    margin-right: 8px;
                    border-radius: 2px;
                }
            }
            
            .pet-info-detail {
                display: flex;
                align-items: center;
                
                .detail-pet-avatar {
                    width: 30px;
                    height: 30px;
                    border-radius: 50%;
                    object-fit: cover;
                    margin-right: 10px;
                    background-color: #f8f9fa;
                }
            }
            
            .owner-info-detail {
                display: flex;
                align-items: center;
                
                .detail-owner-avatar {
                    width: 30px;
                    height: 30px;
                    border-radius: 50%;
                    object-fit: cover;
                    margin-right: 10px;
                    background-color: #f8f9fa;
                }
            }
            
            .value-highlight {
                font-weight: bold;
                color: #409eff;
            }
            
            .value-empty {
                color: #c0c4cc;
                font-style: italic;
            }
            
            .notes-card {
                background: #f8f9fa;
                border: 1px solid #ebeef5;
                
                .notes-text {
                    line-height: 1.6;
                    color: #606266;
                    white-space: pre-wrap;
                }
            }
        }
    }

    /* 图表对话框样式 */
    .chart-dialog-content {
        .pet-info-card {
            margin-bottom: 20px;
            .pet-basic-info {
                display: flex;
                align-items: center;
                .chart-pet-avatar {
                    width: 60px;
                    height: 60px;
                    border-radius: 50%;
                    object-fit: cover;
                    margin-right: 15px;
                    border: 3px solid #f0f0f0;
                    background-color: #f8f9fa;
                }
                .pet-info-text {
                    h3 {
                        margin: 0 0 5px 0;
                        font-size: 18px;
                        color: #303133;
                        font-weight: 600;
                    }
                    .pet-id {
                        margin: 0;
                        color: #909399;
                        font-size: 13px;
                    }
                    .owner-info {
                        margin: 5px 0 0 0;
                        color: #409eff;
                        font-size: 14px;
                        font-weight: 500;
                    }
                }
            }
        }

        .charts-container {
            .chart-card {
                margin-bottom: 20px;
                .chart-card-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    font-weight: 600;
                    span {
                        font-size: 15px;
                        color: #303133;
                    }
                }
                .empty-chart {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 350px;
                    background: #f8f9fa;
                    border-radius: 8px;
                }
            }

            .stats-card {
                .stats-header {
                    font-weight: 600;
                    font-size: 15px;
                    color: #303133;
                }
                .pet-stat-item {
                    padding: 15px;
                    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                    border-radius: 8px;
                    text-align: center;
                    border: 1px solid #dee2e6;
                    .pet-stat-label {
                        font-size: 12px;
                        color: #6c757d;
                        margin-bottom: 5px;
                        font-weight: 500;
                    }
                    .pet-stat-value {
                        font-size: 20px;
                        font-weight: bold;
                        color: #495057;
                    }
                }
            }
        }
    }
}

// 响应式设计
@media (max-width: 768px) {
    .pet-health-manage {
        padding: 15px;
        
        .filter-form {
            .el-form-item {
                margin-bottom: 15px !important;
                width: 100%;
                
                ::v-deep .el-form-item__content {
                    width: 100%;
                }
            }
        }
        
        .quick-stats {
            .el-col {
                margin-bottom: 15px;
            }
        }

        .chart-dialog-content {
            .charts-container {
                .el-col {
                    margin-bottom: 15px;
                }
            }
        }
    }
}

/* 全屏模式样式 */
:deep(.el-dialog__wrapper) {
    &.fullscreen {
        .el-dialog {
            width: 100% !important;
            height: 100% !important;
            margin: 0 !important;
            max-height: none !important;
            .el-dialog__body {
                height: calc(100% - 110px);
                overflow: auto;
            }
        }
    }
}
</style>