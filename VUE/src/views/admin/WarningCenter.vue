<template>
  <div class="warning-center">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <h2 style="margin: 0; float: left">🚨 防抛弃预警中心</h2>
        <div style="float: right">
          <!-- 手动发放问卷按钮 -->
          <el-button 
            type="success" 
            icon="el-icon-plus" 
            @click="openManualDialog" 
            size="small"
            style="margin-right: 10px">
            手动发放问卷
          </el-button>
          <el-button 
            type="primary" 
            icon="el-icon-refresh" 
            @click="refreshAll" 
            :loading="loading"
            size="small">
            刷新
          </el-button>
          <el-radio-group v-model="viewMode" size="small" style="margin-left: 10px">
            <el-radio-button label="records">预警记录</el-radio-button>
            <el-radio-button label="stats">数据统计</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card" @click.native="viewQuestionnaireList('all')">
            <div class="stats-item">
              <div class="stats-icon total">📋</div>
              <div class="stats-info">
                <div class="stats-label">总问卷</div>
                <div class="stats-value">{{ stats.totalQuestionnaires || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card" @click.native="viewQuestionnaireList('pending')">
            <div class="stats-item">
              <div class="stats-icon pending">⏳</div>
              <div class="stats-info">
                <div class="stats-label">待回答</div>
                <div class="stats-value">{{ stats.pendingCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card" @click.native="viewQuestionnaireList('completed')">
            <div class="stats-item">
              <div class="stats-icon completed">✅</div>
              <div class="stats-info">
                <div class="stats-label">已完成</div>
                <div class="stats-value">{{ stats.completedCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card" @click.native="viewWarningList('all')">
            <div class="stats-item">
              <div class="stats-icon risk">⚠️</div>
              <div class="stats-info">
                <div class="stats-label">总预警</div>
                <div class="stats-value">{{ stats.totalWarnings || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card" @click.native="viewWarningList(3)">
            <div class="stats-item">
              <div class="stats-icon high">🔴</div>
              <div class="stats-info">
                <div class="stats-label">高风险</div>
                <div class="stats-value">{{ stats.highRiskCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="4">
          <el-card shadow="hover" class="stats-card" @click.native="viewWarningList(2)">
            <div class="stats-item">
              <div class="stats-icon medium">🟡</div>
              <div class="stats-info">
                <div class="stats-label">中风险</div>
                <div class="stats-value">{{ stats.mediumRiskCount || 0 }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <!-- 模式1：预警记录视图 -->
      <div v-if="viewMode === 'records'">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="预警记录" name="warning">
            <div class="filter-container">
              <el-form :inline="true" :model="warningFilter" @submit.native.prevent>
                <el-form-item label="用户">
                  <el-input v-model="warningFilter.userName" placeholder="用户名" clearable style="width: 120px" @keyup.enter="loadWarningList"></el-input>
                </el-form-item>
                <el-form-item label="宠物">
                  <el-input v-model="warningFilter.petName" placeholder="宠物名" clearable style="width: 120px" @keyup.enter="loadWarningList"></el-input>
                </el-form-item>
                <el-form-item label="预警级别">
                  <el-select v-model="warningFilter.warningLevel" placeholder="全部" clearable style="width: 100px" @change="loadWarningList">
                    <el-option label="低风险" :value="1"></el-option>
                    <el-option label="中风险" :value="2"></el-option>
                    <el-option label="高风险" :value="3"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="warningFilter.status" placeholder="全部" clearable style="width: 100px" @change="loadWarningList">
                    <el-option label="待处理" :value="1"></el-option>
                    <el-option label="已处理" :value="2"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadWarningList">搜索</el-button>
                  <el-button @click="resetWarningFilter">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <el-table :data="warningList" v-loading="loading" border stripe>
              <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
              <el-table-column prop="userName" label="用户" width="100"></el-table-column>
              <el-table-column prop="petName" label="宠物" width="100"></el-table-column>
              <el-table-column prop="warningLevel" label="级别" width="80" align="center">
                <template slot-scope="scope">
                  <el-tag :type="getLevelTagType(scope.row.warningLevel)" size="small">
                    {{ getWarningLevelText(scope.row.warningLevel) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="reason" label="预警原因" min-width="150"></el-table-column>
              <el-table-column prop="suggestion" label="处理建议" min-width="150"></el-table-column>
              <el-table-column prop="createTime" label="创建时间" width="140"></el-table-column>
              <el-table-column prop="status" label="状态" width="80" align="center">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 1 ? 'warning' : 'success'" size="small">
                    {{ scope.row.status === 1 ? '待处理' : '已处理' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template slot-scope="scope">
                  <el-button v-if="scope.row.status === 1" type="primary" size="small" @click="handleProcess(scope.row)" :loading="processingId === scope.row.id">
                    处理
                  </el-button>
                  <el-button v-else type="info" size="small" disabled>已处理</el-button>
                  <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-pagination
              @current-change="handleWarningPageChange"
              :current-page="warningPage.current"
              :page-size="warningPage.size"
              :total="warningPage.total"
              layout="total, prev, pager, next, jumper"
              background
              class="pagination">
            </el-pagination>
          </el-tab-pane>
          
          <el-tab-pane label="问卷记录" name="questionnaire">
            <div class="filter-container">
              <el-form :inline="true" :model="questionnaireFilter">
                <el-form-item label="用户">
                  <el-input v-model="questionnaireFilter.userName" placeholder="用户名" clearable style="width: 120px"></el-input>
                </el-form-item>
                <el-form-item label="宠物">
                  <el-input v-model="questionnaireFilter.petName" placeholder="宠物名" clearable style="width: 120px"></el-input>
                </el-form-item>
                <el-form-item label="状态">
                  <el-select v-model="questionnaireFilter.status" placeholder="全部" clearable style="width: 100px">
                    <el-option label="待回答" :value="1"></el-option>
                    <el-option label="已完成" :value="2"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="loadQuestionnaireList">搜索</el-button>
                  <el-button @click="resetQuestionnaireFilter">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
            
            <el-table :data="questionnaireList" v-loading="loading" border stripe>
              <el-table-column type="index" label="序号" width="60"></el-table-column>
              <el-table-column prop="userName" label="用户" width="120"></el-table-column>
              <el-table-column prop="petName" label="宠物" width="120"></el-table-column>
              <el-table-column prop="questionType" label="问卷类型" width="100">
                <template slot-scope="scope">
                  {{ getQuestionnaireTypeText(scope.row.questionType) }}
                </template>
              </el-table-column>
              
              <el-table-column prop="status" label="状态" width="80">
                <template slot-scope="scope">
                  <!-- 已过期 -->
                  <el-tag v-if="scope.row.status === 3" type="danger" size="small" effect="dark">
                    已过期
                  </el-tag>
                  <!-- 已完成 -->
                  <el-tag v-else-if="scope.row.status === 2" type="success" size="small">
                    已完成
                  </el-tag>
                  <!-- 待回答 -->
                  <el-tag v-else-if="scope.row.status === 1" type="warning" size="small">
                    待回答
                  </el-tag>
                  <!-- 未知状态 -->
                  <el-tag v-else type="info" size="small">
                    未知
                  </el-tag>
                </template>
              </el-table-column>
              
              <el-table-column prop="warningLevel" label="风险等级" width="80">
                <template slot-scope="scope">
                  <el-tag :type="getLevelTagType(scope.row.warningLevel)" size="small">
                    {{ getWarningLevelText(scope.row.warningLevel) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="得分" width="60"></el-table-column>
              <el-table-column prop="createTime" label="发放时间" width="140"></el-table-column>
              <el-table-column prop="submitTime" label="提交时间" width="140"></el-table-column>
              <el-table-column label="操作" width="160" fixed="right">
                <template slot-scope="scope">
                  <el-button 
                    v-if="resendingId !== scope.row.id"
                    type="text" 
                    size="small" 
                    @click="viewQuestionnaireDetail(scope.row)">
                    查看详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <el-pagination
              @current-change="handleQuestionnairePageChange"
              :current-page="questionnairePage.current"
              :page-size="questionnairePage.size"
              :total="questionnairePage.total"
              layout="total, prev, pager, next, jumper"
              background
              class="pagination">
            </el-pagination>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 模式2：数据统计视图 -->
      <div v-else-if="viewMode === 'stats'">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card">
              <div slot="header"><span>预警级别分布</span></div>
              <PieCharts height="300px" :data="levelChartData"></PieCharts>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="chart-card">
              <div slot="header"><span>问卷状态分布</span></div>
              <PieCharts height="300px" :data="statusChartData"></PieCharts>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="24">
            <el-card shadow="hover" class="chart-card">
              <div slot="header"><span>问卷提交趋势</span></div>
              <BarChart height="350px" tag="提交趋势" :values="trendValues" :date="trendDates"></BarChart>
            </el-card>
          </el-col>
        </el-row>
        
        <el-card shadow="hover" class="table-card" style="margin-top: 20px">
          <div slot="header">
            <span>高风险用户列表</span>
            <el-tag type="danger" size="small" style="margin-left: 10px">需重点关注</el-tag>
          </div>
          <el-table :data="highRiskUsers" v-loading="loading" border stripe>
            <el-table-column prop="userName" label="用户" width="120"></el-table-column>
            <el-table-column prop="petName" label="宠物" width="120"></el-table-column>
            <el-table-column prop="reason" label="预警原因" min-width="200"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="140"></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="primary" size="small" @click="goToDetail(scope.row)">处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-card>
    
    <!-- 手动发放问卷弹窗 -->
    <el-dialog 
      title="📝 手动发放问卷" 
      :visible.sync="manualDialogVisible" 
      width="650px"
      @close="resetManualDialog"
      :close-on-click-modal="false">
      
      <el-steps :active="manualStep" finish-status="success" simple style="margin-bottom: 30px">
        <el-step title="选择用户" icon="el-icon-user"></el-step>
        <el-step title="选择模板" icon="el-icon-document"></el-step>
        <el-step title="确认发放" icon="el-icon-check"></el-step>
      </el-steps>
      
      <div v-if="manualStep === 0">
        <div style="margin-bottom: 15px; display: flex; gap: 10px">
          <el-input 
            v-model="userFilter.username"
            placeholder="用户名" 
            clearable 
            style="width: 150px"
            @keyup.enter="loadUsers">
          </el-input>
          <el-input 
            v-model="userFilter.petName" 
            placeholder="宠物名" 
            clearable 
            style="width: 150px"
            @keyup.enter="loadUsers">
          </el-input>
          <el-select v-model="userFilter.warningLevel" placeholder="风险等级" clearable style="width: 120px">
            <el-option label="安全" :value="0"></el-option>
            <el-option label="低风险" :value="1"></el-option>
            <el-option label="中风险" :value="2"></el-option>
            <el-option label="高风险" :value="3"></el-option>
          </el-select>
          <el-button type="primary" @click="loadUsers" :loading="userLoading">搜索</el-button>
        </div>
        
        <el-table 
          :data="userList" 
          v-loading="userLoading"
          @selection-change="handleUserSelectionChange"
          border stripe
          height="350px">
          <el-table-column type="selection" width="55" align="center"></el-table-column>
          <el-table-column prop="username" label="用户" width="100"></el-table-column>
          <el-table-column prop="petName" label="宠物" width="100"></el-table-column>
          <el-table-column prop="warningLevel" label="风险等级" width="80" align="center">
            <template slot-scope="scope">
              <el-tag :type="getLevelTagType(scope.row.warningLevel)" size="small">
                {{ getWarningLevelText(scope.row.warningLevel) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="adoptTime" label="领养时间" width="120">
            <template slot-scope="scope">
              {{ formatDate(scope.row.adoptTime) }}
            </template>
          </el-table-column>
        </el-table>
        
        <div style="margin-top: 15px; text-align: right">
          <el-button @click="manualDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="manualStep = 1" 
            :disabled="selectedUsers.length === 0">
            下一步
          </el-button>
        </div>
      </div>
      
      <div v-if="manualStep === 1">
        <el-table 
          :data="templateList" 
          v-loading="templateLoading"
          @row-click="selectTemplate"
          border stripe
          height="350px">
          <el-table-column width="50" align="center">
            <template slot-scope="scope">
              <el-radio :label="scope.row.id" v-model="selectedTemplate">&nbsp;</el-radio>
            </template>
          </el-table-column>
          <el-table-column prop="templateName" label="模板名称" width="150"></el-table-column>
          <el-table-column prop="templateType" label="类型" width="100">
            <template slot-scope="scope">
              {{ getQuestionnaireTypeText(scope.row.templateType) }}
            </template>
          </el-table-column>
          <el-table-column prop="questionCount" label="问题数量" width="80" align="center"></el-table-column>
          <el-table-column prop="frequency" label="发送频率" width="100"></el-table-column>
        </el-table>
        
        <div style="margin-top: 15px; display: flex; justify-content: space-between">
          <el-button @click="manualStep = 0">上一步</el-button>
          <div>
            <el-button @click="manualDialogVisible = false">取消</el-button>
            <el-button 
              type="primary" 
              @click="manualStep = 2" 
              :disabled="!selectedTemplate">
              下一步
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-if="manualStep === 2">
        <div style="padding: 20px; background: #f9f9f9; border-radius: 4px; margin-bottom: 20px">
          <h4 style="margin-top: 0">📋 发放信息确认</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="发放对象">
              {{ selectedUsers.length }} 个用户/宠物
            </el-descriptions-item>
            <el-descriptions-item label="使用模板">
              {{ getSelectedTemplateName() }}
            </el-descriptions-item>
            <el-descriptions-item label="模板类型">
              {{ getSelectedTemplateType() }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div style="margin-top: 15px; display: flex; justify-content: space-between">
          <el-button @click="manualStep = 1">上一步</el-button>
          <div>
            <el-button @click="manualDialogVisible = false">取消</el-button>
            <el-button 
              type="success" 
              @click="submitManualSend" 
              :loading="sending">
              确认发放
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <el-dialog :title="'预警详情 - ' + currentDetail.petName" :visible.sync="dialogVisible" width="550px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户">{{ currentDetail.userName }}</el-descriptions-item>
        <el-descriptions-item label="宠物">{{ currentDetail.petName }}</el-descriptions-item>
        <el-descriptions-item label="预警级别">
          <el-tag :type="getLevelTagType(currentDetail.warningLevel)">{{ getWarningLevelText(currentDetail.warningLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警原因">{{ currentDetail.reason }}</el-descriptions-item>
        <el-descriptions-item label="处理建议">{{ currentDetail.suggestion }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentDetail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentDetail.status === 1 ? 'warning' : 'success'">
            {{ currentDetail.status === 1 ? '待处理' : '已处理' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { 
  getWarningRecords, 
  processWarning, 
  getWarningStatistics, 
  getAdminQuestionnaireList,
  getAvailableUsers,
  getTemplates,
  manualSendQuestionnaire
} from '@/utils/warning'
import { formatDate } from '@/utils/date'
import PieCharts from '@/components/PieCharts.vue'
import BarChart from '@/components/BarChart.vue'

export default {
  name: 'WarningCenter',
  components: { PieCharts, BarChart },
  data() {
    return {
      // 视图模式：records(记录视图) / stats(统计视图)
      viewMode: 'records',
      
      // 当前激活的标签页：warning(预警记录) / questionnaire(问卷记录)
      activeTab: 'warning',
      
      // 加载状态
      loading: false,
      
      // 正在处理的预警ID（用于按钮loading状态）
      processingId: null,

      // 详情弹窗
      dialogVisible: false,
      currentDetail: {},
      
      // 统计数据
      stats: {
        totalQuestionnaires: 0,
        pendingCount: 0,
        completedCount: 0,
        totalWarnings: 0,
        pendingWarnings: 0,
        lowRiskCount: 0,
        mediumRiskCount: 0,
        highRiskCount: 0,
        avgScore: 0
      },
      
      // 预警列表数据
      warningList: [],
      warningFilter: {
        userName: '',
        petName: '',
        warningLevel: '',
        status: ''
      },
      warningPage: {
        current: 1,
        size: 10,
        total: 0
      },
      
      // 问卷列表数据
      questionnaireList: [],
      questionnaireFilter: {
        userName: '',
        petName: '',
        status: '',
        warningLevel: ''
      },
      questionnairePage: {
        current: 1,
        size: 10,
        total: 0
      },
      
      // 图表数据
      levelChartData: [],
      statusChartData: [],
      trendValues: [],
      trendDates: [],
      currentDays: 30,
      
      // 高风险用户列表
      highRiskUsers: [],
      
      // 手动发放问卷相关
      manualDialogVisible: false,
      manualStep: 0,
      userList: [],
      userLoading: false,
      selectedUsers: [],
      selectedUserIds: [],
      selectedPetIds: [],
      userFilter: {
        userName: '',
        petName: '',
        warningLevel: null
      },
      templateList: [],
      templateLoading: false,
      selectedTemplate: null,
      sending: false
    }
  },
  created() {
    this.loadStatistics()
    this.loadWarningList()
  },
  methods: {
    formatDate,
    
    // 刷新所有数据
    refreshAll() {
      this.loadStatistics()
      if (this.viewMode === 'records') {
        if (this.activeTab === 'warning') {
          this.loadWarningList()
        } else {
          this.loadQuestionnaireList()
        }
      } else {
        this.loadChartData(this.currentDays)
        this.loadHighRiskUsers()
      }
    },
    
    // 加载统计数据
    async loadStatistics() {
      try {
        const res = await getWarningStatistics()
        if (res.code === 200) {
          this.stats = res.data || {}
          
          this.levelChartData = [
            { name: '低风险', value: this.stats.lowRiskCount || 0 },
            { name: '中风险', value: this.stats.mediumRiskCount || 0 },
            { name: '高风险', value: this.stats.highRiskCount || 0 }
          ].filter(item => item.value > 0)
          
          this.statusChartData = [
            { name: '待回答', value: this.stats.pendingCount || 0 },
            { name: '已完成', value: this.stats.completedCount || 0 }
          ].filter(item => item.value > 0)
        }
      } catch (error) {
        console.error('加载统计失败', error)
        this.$message.error('加载统计失败')
      }
    },
    
    // 加载预警列表
    async loadWarningList() {
      this.loading = true
      try {
        const queryDto = { 
          ...this.warningFilter, 
          current: this.warningPage.current, 
          size: this.warningPage.size 
        }
        const res = await getWarningRecords(queryDto)
        if (res.code === 200) {
          this.warningList = res.data || []
          this.warningPage.total = res.total || 0
        } else {
          this.$message.error(res.message || '加载失败')
        }
      } catch (error) {
        console.error('加载预警列表失败', error)
        this.$message.error('加载失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载问卷列表
    async loadQuestionnaireList() {
      this.loading = true
      try {
        const queryDto = {
          ...this.questionnaireFilter,
          current: this.questionnairePage.current,
          size: this.questionnairePage.size
        }
        const res = await getAdminQuestionnaireList(queryDto)
        if (res.code === 200) {
          this.questionnaireList = res.data || []
          this.questionnairePage.total = res.total || 0
        } else {
          this.$message.error(res.message || '加载失败')
        }
      } catch (error) {
        console.error('加载问卷列表失败', error)
        this.$message.error('加载问卷列表失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载高风险用户
    async loadHighRiskUsers() {
      try {
        const queryDto = { 
          warningLevel: 3, 
          status: 1, 
          size: 10, 
          current: 1 
        }
        const res = await getWarningRecords(queryDto)
        if (res.code === 200) {
          this.highRiskUsers = res.data || []
        }
      } catch (error) {
        console.error('加载高风险用户失败', error)
      }
    },
    
    // 加载图表数据
    async loadChartData(days) {
      try {
        const res = await this.$axios.get(`/main/petAdoptOrderCount/${days}`)
        if (res.data) {
          this.trendValues = res.data.map(item => item.count)
          this.trendDates = res.data.map(item => item.name)
        }
      } catch (error) {
        console.error('加载趋势数据失败', error)
        this.generateMockData(days)
      }
    },

    // 生成模拟数据
    generateMockData(days) {
      const dates = []
      const values = []
      const now = new Date()
      
      for (let i = days; i >= 0; i--) {
        const date = new Date(now)
        date.setDate(date.getDate() - i)
        dates.push(formatDate(date).substring(5))
        values.push(Math.floor(Math.random() * 10 + 5))
      }
      
      this.trendDates = dates
      this.trendValues = values
    },
    
    // 处理预警
    handleProcess(row) {
      this.$confirm(`处理"${row.userName} - ${row.petName}"的预警？`, '提示', { 
        type: 'warning',
        confirmButtonText: '确定处理',
        cancelButtonText: '取消'
      }).then(async () => {
        this.processingId = row.id
        try {
          const res = await processWarning(row.id)
          if (res.code === 200) {
            this.$message.success('处理成功')
            this.loadWarningList()
            this.loadStatistics()
          } else {
            this.$message.error(res.message || '处理失败')
          }
        } catch (error) {
          this.$message.error('处理失败')
        } finally {
          this.processingId = null
        }
      }).catch(() => {})
    },
    
    // 查看详情
    viewDetail(row) { 
      this.currentDetail = row
      this.dialogVisible = true 
    },
    
    // 跳转到详情
    goToDetail(row) {
      this.viewMode = 'records'
      this.activeTab = 'warning'
      this.$nextTick(() => {
        this.loadWarningList()
        setTimeout(() => {
          const record = this.warningList.find(item => item.id === row.id)
          if (record) this.viewDetail(record)
        }, 500)
      })
    },
    
    // 查看问卷列表（点击统计卡片）
    viewQuestionnaireList(type) {
      this.viewMode = 'records'
      this.activeTab = 'questionnaire'
      
      if (type === 'pending') {
        this.questionnaireFilter.status = 1
      } else if (type === 'completed') {
        this.questionnaireFilter.status = 2
      } else {
        this.questionnaireFilter.status = ''
      }
      this.questionnaireFilter.warningLevel = ''
      this.questionnairePage.current = 1
      this.loadQuestionnaireList()
    },

    // 查看预警列表（点击统计卡片）
    viewWarningList(level) {
      this.viewMode = 'records'
      this.activeTab = 'warning'
      
      if (level === 'all') {
        this.warningFilter.warningLevel = ''
      } else {
        this.warningFilter.warningLevel = level
      }
      this.warningFilter.status = 1
      this.warningPage.current = 1
      this.loadWarningList()
    },
    
    // 搜索相关
    handleWarningSearch() { 
      this.warningPage.current = 1
      this.loadWarningList() 
    },
    
    resetWarningFilter() { 
      this.warningFilter = { 
        userName: '', 
        petName: '', 
        warningLevel: '', 
        status: '' 
      }
      this.handleWarningSearch() 
    },
    
    handleWarningPageChange(page) { 
      this.warningPage.current = page
      this.loadWarningList() 
    },
    
    handleQuestionnaireSearch() { 
      this.questionnairePage.current = 1
      this.loadQuestionnaireList() 
    },
    
    resetQuestionnaireFilter() { 
      this.questionnaireFilter = { 
        userName: '', 
        petName: '', 
        status: '', 
        warningLevel: '' 
      }
      this.handleQuestionnaireSearch() 
    },
    
    handleQuestionnairePageChange(page) { 
      this.questionnairePage.current = page
      this.loadQuestionnaireList() 
    },
    
    // 标签页切换
    handleTabClick(tab) {
      if (tab.name === 'questionnaire') {
        this.loadQuestionnaireList()
      } else if (tab.name === 'warning') {
        this.loadWarningList()
      }
    },
    
    // 工具函数
    getQuestionnaireTypeText(type) {
      const types = { 
        1: '月度关怀', 
        2: '随机抽查', 
        3: '领养回访' 
      }
      return types[type] || '未知'
    },
    
    getWarningLevelText(level) {
      switch (Number(level)) { 
        case 1: return '低风险'
        case 2: return '中风险'
        case 3: return '高风险'
        default: return '安全'
      }
    },
    
    getLevelTagType(level) {
      switch (Number(level)) { 
        case 1: return 'success'
        case 2: return 'warning'
        case 3: return 'danger'
        default: return 'info'
      }
    },
    
    // 手动发放问卷相关方法
    openManualDialog() {
      this.manualDialogVisible = true
      this.manualStep = 0
      this.loadUsers()
      this.loadTemplates()
    },
    
    resetManualDialog() {
      this.manualStep = 0
      this.userFilter = { userName: '', petName: '', warningLevel: null }
      this.selectedUsers = []
      this.selectedUserIds = []
      this.selectedPetIds = []
      this.selectedTemplate = null
    },
    
// 加载可发放用户列表
async loadUsers() {
  this.userLoading = true
  try {
    // 构建参数，过滤掉空值和null
    const params = {}
    if (this.userFilter.userName && this.userFilter.userName.trim() !== '') {
      params.userName = this.userFilter.userName.trim()
    }
    if (this.userFilter.petName && this.userFilter.petName.trim() !== '') {
      params.petName = this.userFilter.petName.trim()
    }
    if (this.userFilter.warningLevel !== null && this.userFilter.warningLevel !== '') {
      params.warningLevel = this.userFilter.warningLevel
    }
    
    const res = await getAvailableUsers(params)
    if (res.code === 200) {
      this.userList = res.data || []
    } else {
      this.$message.error(res.message || '加载用户失败')
    }
  } catch (error) {
    console.error('加载用户失败', error)
    this.$message.error('加载用户失败')
  } finally {
    this.userLoading = false
  }
},
    
    async loadTemplates() {
      this.templateLoading = true
      try {
        const res = await getTemplates()
        if (res.code === 200) {
          this.templateList = res.data || []
        } else {
          this.$message.error(res.message || '加载模板失败')
        }
      } catch (error) {
        console.error('加载模板失败', error)
        this.$message.error('加载模板失败')
      } finally {
        this.templateLoading = false
      }
    },
    
    handleUserSelectionChange(selection) {
      this.selectedUsers = selection
      this.selectedUserIds = selection.map(item => item.userId)
      this.selectedPetIds = selection.map(item => item.petId)
    },
    
    selectTemplate(row) {
      this.selectedTemplate = row.id
    },
    
    getSelectedTemplateName() {
      if (!this.selectedTemplate) return '未选择'
      const template = this.templateList.find(t => t.id === this.selectedTemplate)
      return template ? template.templateName : '未知模板'
    },
    
    getSelectedTemplateType() {
      if (!this.selectedTemplate) return '未选择'
      const template = this.templateList.find(t => t.id === this.selectedTemplate)
      return template ? this.getQuestionnaireTypeText(template.templateType) : '未知'
    },
    
    async submitManualSend() {
      if (this.selectedUserIds.length === 0) {
        this.$message.warning('请选择发放对象')
        return
      }
      if (!this.selectedTemplate) {
        this.$message.warning('请选择问卷模板')
        return
      }
      
      this.sending = true
      try {
        const sendData = {
          userIds: this.selectedUserIds,
          petIds: this.selectedPetIds,
          templateId: this.selectedTemplate
        }
        
        const res = await manualSendQuestionnaire(sendData)
        if (res.code === 200) {
          this.$message.success('问卷发放成功')
          this.manualDialogVisible = false
          this.loadStatistics()
          if (this.activeTab === 'questionnaire') {
            this.loadQuestionnaireList()
          }
        } else {
          this.$message.error(res.message || '发放失败')
        }
      } catch (error) {
        console.error('发放失败', error)
        this.$message.error('发放失败')
      } finally {
        this.sending = false
      }
    },
    
    // 查看问卷详情
    viewQuestionnaireDetail(row) {
      this.$message.info('问卷详情功能开发中...')
    }
  },
  watch: {
    viewMode(val) {
      if (val === 'stats') {
        this.loadChartData(this.currentDays)
        this.loadHighRiskUsers()
      }
    }
  }
}
</script>

<style scoped>
.warning-center { 
  padding: 20px; 
}
.box-card { 
  min-height: 700px; 
}
.stats-cards { 
  margin-bottom: 20px; 
}
.stats-card { 
  transition: all 0.3s; 
  cursor: pointer; 
}
.stats-card:hover { 
  transform: translateY(-2px); 
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); 
}
.stats-item { 
  display: flex; 
  align-items: center; 
}
.stats-icon { 
  width: 40px; 
  height: 40px; 
  border-radius: 8px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  font-size: 20px; 
  margin-right: 10px; 
}
.stats-icon.total { 
  background: #e6f7ff; 
  color: #1890ff; 
}
.stats-icon.pending { 
  background: #fff7e6; 
  color: #fa8c16; 
}
.stats-icon.completed { 
  background: #f6ffed; 
  color: #52c41a; 
}
.stats-icon.risk { 
  background: #fff1f0; 
  color: #f5222d; 
}
.stats-icon.high { 
  background: #fff1f0; 
  color: #f5222d; 
}
.stats-icon.medium { 
  background: #fffbe6; 
  color: #faad14; 
}
.stats-info { 
  flex: 1; 
}
.stats-label { 
  font-size: 12px; 
  color: #666; 
  margin-bottom: 2px; 
}
.stats-value { 
  font-size: 20px; 
  font-weight: bold; 
  color: #333; 
}
.filter-container { 
  margin: 20px 0; 
  padding: 15px; 
  background: #fafafa; 
  border-radius: 4px; 
}
.pagination { 
  margin-top: 20px; 
  text-align: right; 
}
.chart-card { 
  margin-top: 20px; 
}
.chart-container { 
  padding: 10px; 
}
.table-card { 
  margin-top: 20px; 
}
</style>