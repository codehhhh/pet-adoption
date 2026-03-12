export function formatDate(date) {
    if (!date) return '';
    try {
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    } catch (error) {
        console.error('日期格式化错误:', error);
        return '';
    }
}

export function formatDateTime(date) {
    if (!date) return '';
    try {
        const d = new Date(date);
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
}

export function timeAgo(dateString) {
    const now = new Date();
    const date = new Date(dateString);
    const secondsPast = (now.getTime() - date.getTime()) / 1000;
    if (secondsPast < 60) {
        return `${Math.floor(secondsPast)} 秒前`;
    } else if (secondsPast < 3600) {
        return `${Math.floor(secondsPast / 60)} 分钟前`;
    } else if (secondsPast <= 86400) {
        return `${Math.floor(secondsPast / 3600)} 小时前`;
    } else {
        const daysPast = Math.floor(secondsPast / 86400);
        if (daysPast === 1) {
            return '1 天前';
        } else {
            return `${daysPast} 天前`;
        }
    }
}