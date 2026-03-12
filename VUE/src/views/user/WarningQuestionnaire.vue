<template>
  <div class="warning-questionnaire">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <h2 style="margin: 0">📋 防抛弃预警问卷</h2>
        <p class="sub-title">为了宠物的幸福生活，请认真填写以下问卷</p>
      </div>
      
      <!-- 待回答问卷列表 -->
      <div v-if="pendingList.length > 0">
        <el-alert
          title="您有待回答的问卷"
          type="info"
          :closable="false"
          show-icon>
        </el-alert>
        
        <div v-for="questionnaire in pendingList" :key="questionnaire.id" class="questionnaire-item">
          <el-card shadow="hover" style="margin-top: 20px">
            <div slot="header" class="questionnaire-header">
              <div style="display: flex; align-items: center; gap: 10px">
                <span style="font-weight: 500">{{ questionnaire.petName }}的{{ getQuestionnaireType(questionnaire.questionType) }}问卷</span>
                <el-tag type="info" size="small" v-if="questionnaire.deadline">
                  截止：{{ formatDate(questionnaire.deadline) }}
                </el-tag>
              </div>
              <el-tag type="warning" size="small">待填写</el-tag>
            </div>
            
            <!-- 显示剩余天数提示 -->
            <div v-if="questionnaire.deadline" class="deadline-tip" :class="{ urgent: isUrgent(questionnaire.deadline) }">
              <i class="el-icon-time"></i>
              还剩 {{ getRemainingDays(questionnaire.deadline) }} 天
            </div>
            
            <div class="questionnaire-content">
              <!-- 显示问题列表 -->
              <div v-if="questionnaire && questionnaire.questionList && questionnaire.questionList.length > 0">
                <div v-for="(question, index) in questionnaire.questionList" :key="question.id" class="question-item">
                  <p class="question-title">{{ index + 1 }}. {{ question.question }}</p>
                  
                  <!-- 单选题 - 修复 v-model 绑定 -->
                  <div v-if="question.type === 'radio'" class="options">
                    <el-radio-group 
                      :value="getAnswer(questionnaire.id, question.id)" 
                      @input="setAnswer(questionnaire.id, question.id, $event)">
                      <el-radio v-for="opt in question.options" :key="opt" :label="opt">
                        {{ opt }}
                      </el-radio>
                    </el-radio-group>
                  </div>
                  
                  <!-- 多选题 - 修复 v-model 绑定 -->
                  <div v-else-if="question.type === 'checkbox'" class="options">
                    <el-checkbox-group 
                      :value="getAnswer(questionnaire.id, question.id) || []" 
                      @input="setAnswer(questionnaire.id, question.id, $event)">
                      <el-checkbox v-for="opt in question.options" :key="opt" :label="opt">
                        {{ opt }}
                      </el-checkbox>
                    </el-checkbox-group>
                  </div>
                  
                  <!-- 文本题 - 修复 v-model 绑定 -->
                  <div v-else-if="question.type === 'text'" class="options">
                    <el-input 
                      type="textarea" 
                      :value="getAnswer(questionnaire.id, question.id)" 
                      @input="setAnswer(questionnaire.id, question.id, $event)"
                      :rows="3" 
                      placeholder="请输入您的回答">
                    </el-input>
                  </div>
                </div>
              </div>
              <div v-else class="no-questions">
                <el-empty description="该问卷暂无问题" :image-size="100"></el-empty>
              </div>
              
              <!-- 提交按钮区域 -->
              <div style="margin-top: 20px; padding-top: 15px; border-top: 1px solid #f0f0f0; text-align: center">
                <el-button type="primary" @click="submitQuestionnaire(questionnaire.id)" :loading="submittingId === questionnaire.id">
                  提交问卷
                </el-button>
                <el-button @click="saveDraft(questionnaire.id)">保存草稿</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
      
      <!-- 无待回答问卷 -->
      <div v-else class="empty-state">
        <el-empty description="暂无待回答问卷" :image-size="200">
          <p>您已完成所有问卷，感谢您的配合！</p>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getPendingQuestionnaires, submitQuestionnaire } from '@/utils/warning'
import { formatDate } from '@/utils/date'

export default {
  name: 'WarningQuestionnaire',
  data() {
    return {
      pendingList: [],
      answers: {}, // 存储所有问卷的答案 { [questionnaireId]: { [questionId]: answer } }
      submittingId: null
    }
  },
  created() {
    this.loadPendingQuestionnaires()
  },
  methods: {
    formatDate,
    
    // 获取指定问卷和问题的答案
    getAnswer(questionnaireId, questionId) {
      if (!this.answers[questionnaireId]) {
        return questionId.startsWith('checkbox') ? [] : ''
      }
      const answer = this.answers[questionnaireId][questionId]
      return answer !== undefined ? answer : (questionId.startsWith('checkbox') ? [] : '')
    },
    
    // 设置指定问卷和问题的答案
    setAnswer(questionnaireId, questionId, value) {
      if (!this.answers[questionnaireId]) {
        this.$set(this.answers, questionnaireId, {})
      }
      this.$set(this.answers[questionnaireId], questionId, value)
    },
    
    // 加载待回答问卷
    async loadPendingQuestionnaires() {
      try {
        const response = await getPendingQuestionnaires()
        console.log('待回答问卷响应:', response)
        
        // 处理未登录情况
        if (response.code === 400 && response.message === '用户未登录') {
          this.$message.warning('请先登录后再查看问卷')
          setTimeout(() => {
            this.$router.push('/login')
          }, 3000)
          this.pendingList = []
          return
        }
        
        // 处理其他错误
        if (!response || response.code !== 200) {
          this.$message.error(response.message || '加载问卷失败')
          this.pendingList = []
          return
        }
        
        // 成功获取数据
        this.pendingList = response.data || []
        
        // 初始化答案对象
        if (this.pendingList && this.pendingList.length > 0) {
          this.pendingList.forEach(questionnaire => {
            if (!this.answers[questionnaire.id]) {
              this.$set(this.answers, questionnaire.id, {})
            }
            
            // 尝试从localStorage加载草稿
            const draft = localStorage.getItem(`questionnaire_draft_${questionnaire.id}`)
            let draftAnswers = {}
            if (draft) {
              try {
                draftAnswers = JSON.parse(draft)
              } catch (e) {
                console.error('解析草稿失败', e)
              }
            }
            
            // 初始化每个问题的答案
            if (questionnaire.questionList && questionnaire.questionList.length > 0) {
              questionnaire.questionList.forEach(q => {
                if (q && q.id) {
                  if (draftAnswers[q.id] !== undefined) {
                    this.$set(this.answers[questionnaire.id], q.id, draftAnswers[q.id])
                  } else {
                    // 根据问题类型设置默认值
                    if (q.type === 'checkbox') {
                      this.$set(this.answers[questionnaire.id], q.id, [])
                    } else {
                      this.$set(this.answers[questionnaire.id], q.id, '')
                    }
                  }
                }
              })
            }
          })
        }
      } catch (error) {
        console.error('加载问卷异常:', error)
        this.$message.error('加载问卷失败，请稍后重试')
        this.pendingList = []
      }
    },
    
    // 判断是否紧急（剩余少于2天）
    isUrgent(deadline) {
      if (!deadline) return false
      const days = this.getRemainingDays(deadline)
      return days <= 2
    },
    
    // 获取剩余天数
    getRemainingDays(deadline) {
      if (!deadline) return 0
      const now = new Date()
      const end = new Date(deadline)
      const diff = end - now
      return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
    },
    
    // 获取问卷类型文字
    getQuestionnaireType(type) {
      const types = {
        1: '定期关怀',
        2: '随机抽查',
        3: '预警触发'
      }
      return types[type] || '未知类型'
    },
    
    // 提交问卷
    async submitQuestionnaire(questionnaireId) {
      try {
        this.submittingId = questionnaireId
        
        // 获取当前问卷
        const currentQuestionnaire = this.pendingList.find(q => q.id === questionnaireId)
        if (!currentQuestionnaire) {
          this.$message.error('问卷不存在')
          return
        }
        
        // 检查是否所有问题都已回答
        const questionList = currentQuestionnaire.questionList || []
        const questionnaireAnswers = this.answers[questionnaireId] || {}
        
        const unanswered = questionList.filter(q => {
          const answer = questionnaireAnswers[q.id]
          if (q.type === 'checkbox') {
            return !answer || answer.length === 0
          } else {
            return answer === undefined || answer === null || answer === ''
          }
        })
        
        if (unanswered.length > 0) {
          this.$message.warning(`请先回答所有问题，还有${unanswered.length}个问题未回答`)
          this.submittingId = null
          return
        }
        
        // 转换答案为JSON字符串
        const answersJson = JSON.stringify(questionnaireAnswers)
        
        const submitData = {
          questionnaireId: questionnaireId,
          answers: answersJson
        }
        
        const response = await submitQuestionnaire(submitData)
        
        if (response && response.code === 200) {
          this.$message.success('问卷提交成功！')
          // 清除草稿
          localStorage.removeItem(`questionnaire_draft_${questionnaireId}`)
          // 重新加载列表
          await this.loadPendingQuestionnaires()
        } else {
          this.$message.error((response && response.message) || '提交失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        this.$message.error('提交失败，请稍后重试')
      } finally {
        this.submittingId = null
      }
    },
    
    // 保存草稿
    saveDraft(questionnaireId) {
      try {
        const questionnaireAnswers = this.answers[questionnaireId] || {}
        localStorage.setItem(`questionnaire_draft_${questionnaireId}`, JSON.stringify(questionnaireAnswers))
        this.$message.success('草稿已保存')
      } catch (error) {
        console.error('保存草稿失败:', error)
        this.$message.error('保存草稿失败')
      }
    }
  }
}
</script>

<style scoped>
.warning-questionnaire {
  padding: 20px;
}

.box-card {
  min-height: 500px;
}

.sub-title {
  color: #666;
  margin: 5px 0 0 0;
  font-size: 14px;
}

.questionnaire-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.deadline-tip {
  margin: 10px 0;
  padding: 5px 10px;
  background-color: #f0f9eb;
  border-radius: 4px;
  font-size: 13px;
  color: #67c23a;
}

.deadline-tip.urgent {
  background-color: #fef0f0;
  color: #f56c6c;
}

.question-item {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.question-item:last-child {
  border-bottom: none;
}

.question-title {
  font-weight: 500;
  margin-bottom: 10px;
  color: #333;
}

.options {
  margin-left: 20px;
}

.no-questions {
  padding: 30px 0;
}

.empty-state {
  text-align: center;
  padding: 50px 0;
}
</style>