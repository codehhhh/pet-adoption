import { get, post } from './request'

/**
 * 预警中心API
 * 文件位置：src/utils/warning.js
 */

// 安全获取嵌套属性（ES5兼容）
function safeGet(obj, path, defaultValue = null) {
    if (!obj) return defaultValue
    const keys = path.split('.')
    let result = obj
    for (let i = 0; i < keys.length; i++) {
        const key = keys[i]
        if (result && typeof result === 'object' && key in result) {
            result = result[key]
        } else {
            return defaultValue
        }
    }
    return result
}

// 获取用户待回答的问卷
export function getPendingQuestionnaires() {
    return get('/warning/questionnaire/pending')
        .catch(function (error) {
            console.error('获取问卷失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取问卷失败'),
                data: []
            }
        })
}

// 提交问卷答案
export function submitQuestionnaire(data) {
    return post('/warning/questionnaire/submit', data)
        .catch(function (error) {
            console.error('提交问卷失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '提交失败'),
                data: null
            }
        })
}

// 获取用户的历史问卷
export function getQuestionnaireHistory() {
    return get('/warning/questionnaire/history')
        .catch(function (error) {
            console.error('获取历史问卷失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取历史失败'),
                data: []
            }
        })
}

// 获取预警记录列表（管理员）
export function getWarningRecords(data) {
    return post('/warning/record/list', data)
        .catch(function (error) {
            console.error('获取预警记录失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取记录失败'),
                data: { records: [], total: 0 }
            }
        })
}

// 处理预警记录
export function processWarning(warningId) {
    return post('/warning/record/process/' + warningId)
        .catch(function (error) {
            console.error('处理预警失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '处理失败'),
                data: null
            }
        })
}

// 获取预警统计数据
export function getWarningStatistics() {
    return get('/warning/statistics')
        .catch(function (error) {
            console.error('获取统计失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取统计失败'),
                data: null
            }
        })
}

/**
 * 获取管理员统计概览
 */
export function getAdminStatistics() {
    return get('/warning/admin/statistics')
        .catch(function (error) {
            console.error('获取统计概览失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取统计失败'),
                data: null
            }
        })
}

/**
 * 获取管理员问卷列表
 * @param {Object} data - 查询参数
 */
export function getAdminQuestionnaireList(data) {
    return post('/warning/admin/questionnaire/list', data)
        .catch(function (error) {
            console.error('获取问卷列表失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取列表失败'),
                data: [],
                total: 0
            }
        })
}

// ==================== 新增：手动发放问卷相关接口 ====================

/**
 * 获取可发放问卷的用户列表（管理员）
 * @param {Object} params - 查询参数 { userName, petName, riskLevel }
 */
export function getAvailableUsers(params) {
    return get('/warning/admin/available-users', params)
        .catch(function (error) {
            console.error('获取可发放用户失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取用户失败'),
                data: []
            }
        })
}

/**
 * 获取问卷模板列表
 */
export function getTemplates() {
    return get('/warning/admin/templates')
        .catch(function (error) {
            console.error('获取模板失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '获取模板失败'),
                data: []
            }
        })
}

/**
 * 手动发放问卷
 * @param {Object} data - 发放数据 { userIds: [], petIds: [], templateId: number, questionType: number }
 */
export function manualSendQuestionnaire(data) {
    return post('/warning/admin/manual-send', data)
        .catch(function (error) {
            console.error('手动发放失败:', error)
            return {
                code: safeGet(error, 'response.status', 500),
                message: safeGet(error, 'response.data.message', '发放失败'),
                data: null
            }
        })
}