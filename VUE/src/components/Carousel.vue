<template>
    <div class="hero-carousel-container" :style="{ height: containerHeight }">
        <!-- 主轮播区域 -->
        <div class="main-carousel" :style="{ backgroundImage: 'url(' + safeImageUrl(currentItem.image) + ')' }">
            <div class="carousel-content">
                <h1 class="carousel-title">{{ currentItem.title }}</h1>
                <p class="carousel-subtitle">{{ currentItem.subtitle }}</p>
                <button class="detail-btn customer" @click="handleDetail(currentItem)">查看详情</button>
            </div>

            <!-- 图片指示器（右下角绝对定位） -->
            <div class="thumbnail-indicator">
                <div v-for="(item, index) in carouselItems" :key="index" class="thumbnail-item"
                    :class="{ active: currentCarouselIndex === index }" @click="changeCarousel(index)">
                    <img :src="safeImageUrl(item.image)" :alt="item.title" @error="handleImageError">
                </div>
            </div>
        </div>

        <!-- 分类标签（底部居中） -->
        <div class="category-tabs" v-if="tabs.length !== 0">
            <div v-for="(tab, index) in tabs" :key="index" class="tab-item" :class="{ active: activeTab === tab.value }"
                @click="handleTabClick(tab)">
                {{ tab.label }}
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: "HeroCarousel",
    props: {
        containerHeight: {
            type: String,
            default: '60vh'
        },
        carouselItems: {
            type: Array,
            default: () => [],
            validator: value => value.every(item => item.image && item.title && item.subtitle)
        },
        tabs: {
            type: Array,
            default: () => []
        },
        interval: {
            type: Number,
            default: 5000
        }
    },
    data() {
        return {
            currentCarouselIndex: 0,
            activeTab: this.tabs.length > 0 ? this.tabs[0].value : null,
            carouselInterval: null
        }
    },
    computed: {
        currentItem() {
            return this.carouselItems[this.currentCarouselIndex] || {}
        }
    },
    mounted() {
        this.startCarousel()
    },
    beforeDestroy() {
        clearInterval(this.carouselInterval)
    },
    methods: {
        // ==================== 修复：图片安全处理方法 ====================
        getDefaultCarouselImage() {
            // 使用简单的纯英文SVG，避免中文编码问题
            return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4NCiAgPGRlZnM+DQogICAgPGxpbmVhckdyYWRpZW50IGlkPSJiZ0dyYWRpZW50IiB4MT0iMCUiIHkxPSIwJSIgeDI9IjEwMCUiIHkyPSIxMDAlIj4NCiAgICAgIDxzdG9wIG9mZnNldD0iMCUiIHN0eWxlPSJzdG9wLWNvbG9yOiM0ZmFjZmU7c3RvcC1vcGFjaXR5OjEiIC8+DQogICAgICA8c3RvcCBvZmZzZXQ9IjEwMCUiIHN0eWxlPSJzdG9wLWNvbG9yOiMwMGYyZmU7c3RvcC1vcGFjaXR5OjEiIC8+DQogICAgPC9saW5lYXJHcmFkaWVudD4NCiAgPC9kZWZzPg0KICA8cmVjdCB3aWR0aD0iODAwIiBoZWlnaHQ9IjQwMCIgZmlsbD0idXJsKCNiZ0dyYWRpZW50KSIgLz4NCiAgPHRleHQgeD0iNTAlIiB5PSI0NSUiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IndoaXRlIiBmb250LXNpemU9IjI0IiBmb250LWZhbWlseT0iQXJpYWwiPkNhcm91c2VsIEltYWdlPC90ZXh0Pg0KICA8dGV4dCB4PSI1MCUiIHk9IjU1JSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0id2hpdGUiIGZvbnQtc2l6ZT0iMTYiIGZvbnQtZmFtaWx5PSJBcmlhbCI+TG9hZGluZy4uLjwvdGV4dD4NCjwvc3ZnPg==';
        },
        
        // 正确的Base64编码函数
        encodeToBase64(str) {
            try {
                // 使用UTF-8安全编码
                return btoa(unescape(encodeURIComponent(str)));
            } catch (error) {
                console.error('Base64编码失败:', error);
                // 返回一个简单的默认图片
                return 'PHN2ZyB3aWR0aD0iODAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iODAwIiBoZWlnaHQ9IjQwMCIgZmlsbD0iI2YwZjBmMCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTk5IiBmb250LXNpemU9IjE2Ij5DYXJvdXNlbDwvdGV4dD48L3N2Zz4=';
            }
        },
        
        safeImageUrl(url) {
            // 安全处理图片URL
            if (!url || url === 'undefined' || url === 'null') {
                return this.getDefaultCarouselImage();
            }
            
            // 如果是Base64或完整URL，直接返回
            if (url.startsWith('data:') || url.startsWith('http://') || url.startsWith('https://')) {
                return url;
            }
            
            // 如果是相对路径或文件名，确保正确处理
            if (url.includes('getFile?fileName=')) {
                // 已经是完整路径，直接返回
                return url;
            }
            
            // 其他情况，可能是文件名，需要拼接路径
            if (url && !url.includes('/') && url.trim() !== '') {
                // 如果是文件名，拼接完整路径
                return `/getFile?fileName=${encodeURIComponent(url)}`;
            }
            
            // 如果以上都不是，返回默认图片
            return this.getDefaultCarouselImage();
        },
        
        handleImageError(event) {
            // 图片加载失败时使用默认图片
            event.target.src = this.getDefaultCarouselImage();
            event.target.onerror = null;
        },
        // ==================== 修复结束 ====================
        
        handleTabClick(tab) {
            this.activeTab = tab.value;
            this.$emit('tab-change', tab);
        },
        
        handleDetail(obj) {
            this.$emit('obj-detail', obj);
        },
        
        changeCarousel(index) {
            this.currentCarouselIndex = index;
            this.resetCarousel();
        },
        
        startCarousel() {
            if (this.carouselItems.length <= 1) return;

            this.carouselInterval = setInterval(() => {
                this.currentCarouselIndex =
                    (this.currentCarouselIndex + 1) % this.carouselItems.length;
            }, this.interval);
        },
        
        resetCarousel() {
            clearInterval(this.carouselInterval);
            this.startCarousel();
        }
    }
}
</script>

<style scoped lang="scss">
.hero-carousel-container {
    position: relative;
    min-width: 420px;
    min-height: 300px;
    overflow: hidden;

    .main-carousel {
        position: relative;
        width: 100%;
        height: 100%;
        background-size: cover;
        background-position: center;
        transition: background-image 0.8s ease;
        background-color: #f0f0f0; // 添加背景色，防止透明

        &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
        }

        .carousel-content {
            position: absolute;
            top: 50%;
            left: 10%;
            transform: translateY(-50%);
            color: white;
            max-width: 500px;
            z-index: 2;

            .carousel-title {
                font-size: 3rem;
                padding-left: 10px;
                width: 600px;
                text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
                animation: fadeInUp 0.8s ease;
            }

            .carousel-subtitle {
                font-size: 1.5rem;
                width: 600px;
                padding-left: 10px;
                text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
                animation: fadeInUp 0.8s ease 0.2s forwards;
                opacity: 0;
            }

            .detail-btn {
                background-color: #FFD700;
                color: #333;
                border: none;
                padding: 12px 30px;
                font-size: 1rem;
                border-radius: 30px;
                cursor: pointer;
                transition: all 0.3s ease;
                animation: fadeInUp 0.8s ease 0.4s forwards;
                opacity: 0;

                &:hover {
                    background-color: darken(#FFD700, 10%);
                    transform: translateY(-2px);
                    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
                }
            }
        }

        /* 图片指示器样式（右下角绝对定位） */
        .thumbnail-indicator {
            position: absolute;
            right: 20px;
            bottom: 80px;
            display: flex;
            gap: 10px;
            z-index: 3;
            background: rgba(0, 0, 0, 0.6);
            padding: 10px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

            .thumbnail-item {
                width: 60px;
                height: 40px;
                border-radius: 8px;
                overflow: hidden;
                cursor: pointer;
                transition: all 0.3s ease;
                position: relative;
                border: 2px solid transparent;

                img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    background-color: #f8f9fa;
                }

                &.active {
                    border-color: rgb(0, 165, 120);
                    transform: scale(1.1);
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
                }

                &:hover:not(.active) {
                    transform: scale(1.05);
                    opacity: 0.9;
                }
            }
        }
    }

    /* 分类标签样式（底部居中） */
    .category-tabs {
        position: absolute;
        bottom: 20px;
        left: 50%;
        transform: translateX(-50%);
        display: flex;
        background: rgba(0, 0, 0, 0.7);
        border-radius: 30px;
        padding: 5px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        z-index: 2;

        .tab-item {
            padding: 10px 25px;
            border-radius: 25px;
            color: rgb(255, 255, 255);
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 500;

            &.active {
                background: rgb(255, 255, 255);
                color: rgb(51, 51, 51);
            }

            &:hover {
                transform: translateY(-2px);
            }
        }
    }
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 响应式设计 */
@media (max-width: 768px) {
    .hero-carousel-container {
        height: 70vh;

        .main-carousel {
            .carousel-content {
                left: 5%;
                right: 5%;
                max-width: 100%;
                text-align: center;

                .carousel-title {
                    font-size: 2rem;
                }

                .carousel-subtitle {
                    font-size: 1.2rem;
                }
            }

            .thumbnail-indicator {
                right: 10px;
                bottom: 70px;
                padding: 8px;

                .thumbnail-item {
                    width: 50px;
                    height: 35px;
                }
            }
        }

        .category-tabs {
            .tab-item {
                padding: 8px 15px;
                font-size: 0.9rem;
            }
        }
    }
}

@media (max-width: 480px) {
    .hero-carousel-container {
        height: 60vh;

        .main-carousel {
            .thumbnail-indicator {
                right: 5px;
                bottom: 60px;
                gap: 5px;

                .thumbnail-item {
                    width: 40px;
                    height: 30px;
                }
            }
        }

        .category-tabs {
            .tab-item {
                padding: 6px 12px;
                font-size: 0.8rem;
            }
        }
    }
}
</style>