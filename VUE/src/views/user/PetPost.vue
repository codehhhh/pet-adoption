<template>
    <div class="pet-post">
        <!-- 帖子数据 -->
        <div class="pet">
            <div class="pet-type">
                <div :class="{ 'active': petTypeId === petType.id }" @click="petTypeClick(petType)"
                    class="pet-type-item" v-for="petType in petTypeList" :key="petType.id">
                    {{ petType.name }}
                </div>
                <div>
                    <AutoInput placeholder="搜索宠物经验帖子" @listener="listener" />
                </div>
            </div>
                        <!-- 新增：置顶板块（显示在推荐下方） -->
            <div v-if="topPostList.length > 0" class="top-post-section" style="width: 100%; margin-top: 20px;">
                <h2 style="margin-bottom: 15px; color: #333; border-left: 4px solid #409EFF; padding-left: 10px;">
                    <i class="el-icon-top"></i> 置顶推荐
                </h2>
                <div class="top-post-list" style="display: flex; flex-wrap: wrap; gap: 20px; margin-bottom: 30px;">
                    <div @click="goDetail(post)" class="top-post-item" 
                         v-for="(post, index) in topPostList" :key="index"
                         style="width: calc(33.333% - 20px); border: 2px solid #409EFF; border-radius: 8px; padding: 15px; cursor: pointer; background: #f0f9ff;">
                        <div style="position: relative;">
                            <div style="position: absolute; top: 10px; right: 10px; background: #409EFF; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px;">
                                <i class="el-icon-top"></i> 置顶
                            </div>
                            <img style="width: 100%; height: 180px; border-radius: 5px; object-fit: cover;" 
                                 :src="post.cover" alt="">
                        </div>
                        <div style="margin-top: 10px;">
                            <div style="font-size: 16px; margin-bottom: 8px; font-weight: bold; color: #333;">
                                {{ post.title }}
                            </div>
                            <div style="display: flex; align-items: center; margin-bottom: 8px;">
                                <img style="width: 25px; height: 25px; border-radius: 50%; margin-right: 8px;" 
                                     :src="post.avatar" alt="">
                                <span style="font-size: 14px; color: #666;">{{ post.username }}</span>
                            </div>
                            <div style="font-size: 12px; color: #999; display: flex; gap: 15px;">
                                <span><i class="el-icon-thumb"></i> {{ post.upvoteNumber || 0 }} 点赞</span>
                                <span><i class="el-icon-view"></i> {{ post.viewNumber || 0 }} 浏览</span>
                                <span><i class="el-icon-star-off"></i> {{ post.saveNumber || 0 }} 收藏</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 宠物列表区域 -->
            <div class="pet-list">
                <!-- 空状态 -->
                <div v-if="petPostList.length === 0" key="empty">
                    <el-empty description="该类别下，暂无宠物经验帖子信息"></el-empty>
                </div>
                <div>
                    <div @click="goDetail(petPost)" class="pet-post-item" v-for="(petPost, index) in petPostList"
                        :key="index">
                        <div style="display: flex;gap: 10px;box-sizing: border-box;">
                            <div>
                                <img style="width: 220px;height: 140px;border-radius: 5px;" :src="petPost.cover" alt="">
                            </div>
                            <div>
                                <div style="font-size: 18px;margin-bottom: 6px;">{{ petPost.title }}</div>
                                <div
                                    style="margin-bottom: 6px;display: flex;gap: 10px;justify-content: left;align-items: center;">
                                    <img style="width: 25px;height: 25px;border-radius: 50%;" :src="petPost.avatar"
                                        alt="">
                                    <span>{{ petPost.username }}</span>
                                </div>
                                <div
                                    style="padding: 10px;background-color: rgb(246,247,248);margin-bottom: 6px;font-size: 14px;">
                                    {{ petPost.summary }}
                                </div>
                                <div style="display: flex;gap: 10px;font-size: 12px;">
                                    <span>
                                        点赞量({{ petPost.upvoteNumber || 0 }})
                                    </span>
                                    <span>
                                        浏览量({{ petPost.viewNumber || 0 }})
                                    </span>
                                    <span>
                                        收藏量({{ petPost.saveNumber || 0 }})
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 推荐区域 -->
        <div class="top">
            <div class="tip">推荐</div>
            <div @click="goDetail(item)" class="pet-post-recommend" v-for="(item, index) in recommendPetPostList"
                :key="index">
                <div>
                    <img :src="item.cover" alt="" srcset="">
                </div>
                <div class="title">
                    {{ item.title }}
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import AutoInput from "@/components/AutoInput.vue"; // 导入封装好的输入框组件
export default {
    components: { AutoInput },
    name: 'Home',
    data() {
        return {
            petTypeList: [],
            petTypeId: null,
            petPostQueryDto: {},
            petPostList: [],
            recommendPetPostList: [],
            topPostList: []// 新增：置顶帖子列表
        }
    },
    created() {
        this.fetchPetPostType();
        this.fetchRecommendPetPostList();
        this.fetchTopPostList(); // 新增：获取置顶帖子
    },
    methods: {
        listener(text) {
            this.petPostQueryDto.title = text;
            this.fetchPetPost();
        },
        petTypeClick(petType) {
            this.petTypeId = petType.id;
            this.petPostQueryDto.petTypeId = petType.id;
            this.fetchPetPost();
        },
        async fetchRecommendPetPostList() {
            try {
                const { data } = await this.$axios.get(`/pet-post/autoRecommend/${3}`);
                this.recommendPetPostList = data;
            } catch (error) {
                console.log("查询推荐宠物经验信息异常：", error);
            }
        },
        async fetchPetPostType() {
            try {
                const { data } = await this.$axios.post('/pet-type/query', {});
                this.petTypeList = data;
                this.petTypeList.unshift({ id: null, name: '全部' });
                if (this.petTypeList.length > 0) {
                    this.petTypeClick(this.petTypeList[0]);
                }
            } catch (error) {
                console.log("查询宠物类别信息异常：", error);
            }
        },
        async fetchPetPost() {
            try {
                const { data } = await this.$axios.post('/pet-post/list', this.petPostQueryDto);
                this.petPostList = data;
            } catch (error) {
                console.log("查询宠物经验帖子信息异常：", error);
            }
        },
        async fetchTopPostList() {
            try {
                const { data } = await this.$axios.get('/pet-post/top');
                this.topPostList = data;
            } catch (error) {
                console.log("查询置顶宠物经验帖子信息异常：", error);
            }
        },
        goDetail(petPost) {
            window.open(`pet-post-detail?id=${petPost.id}`, '_blank');
        }
    }
}
</script>

<style scoped lang="scss">
.pet-post {
    display: flex;
    justify-content: left;
}

.top {
    padding: 0 20px;
    box-sizing: border-box;

    .tip {
        font-size: 24px;
        font-weight: 600;
        margin-bottom: 20px;
    }

    .pet-post-recommend {
        img {
            width: 100%;
            height: 180px;
            border-radius: 5px;
        }

        .title {
            font-size: 16px;
            margin: 10px 0 20px 0;
            cursor: pointer;
        }
    }
}

.pet {
    width: 90%;

    .pet-type {
        margin-block: 20px;
        display: flex;
        justify-content: left;
        align-items: center;
        gap: 10px;
        flex-wrap: wrap;

        .pet-type-item {
            cursor: pointer;
            padding: 8px 16px;
            border-radius: 10px;
            transition: all 0.3s ease;

            &:hover {
                background-color: rgb(240, 240, 240);
            }

            &.active {
                background-color: #1a933e;
                color: white;
            }
        }
    }

    .pet-list {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        margin-top: 20px;

        .pet-post-item {
            padding: 10px;
            box-sizing: border-box;
            cursor: pointer;

            &:hover {
                box-shadow: 0 4px 8px rgb(230, 230, 230);
            }
        }


        .pet-card {
            width: calc((100% - 100px) / 6); // 6个卡片每行
            min-width: 180px;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            cursor: pointer;
            background: white;

            &:hover {
                transform: translateY(-5px);
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            }

            .pet-cover {
                position: relative;
                height: 180px;
                overflow: hidden;

                img {
                    width: 100%;
                    height: 100%;
                    object-fit: cover;
                    transition: transform 0.5s ease;
                }

                &:hover img {
                    transform: scale(1.05);
                }

                .adopted-tag,
                .recommend-tag {
                    position: absolute;
                    top: 10px;
                    padding: 2px 8px;
                    border-radius: 4px;
                    color: white;
                    font-size: 12px;
                }

                .adopted-tag {
                    right: 10px;
                    background-color: #f56c6c;
                }

                .recommend-tag {
                    left: 10px;
                    background-color: #1a933e;
                }
            }

            .pet-info {
                padding: 15px;

                h3 {
                    margin: 0 0 8px;
                    font-size: 16px;
                    color: #333;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }

                .pet-meta {
                    display: flex;
                    gap: 8px;
                    margin-bottom: 8px;
                    font-size: 12px;
                    color: #666;
                }

                .pet-location {
                    font-size: 12px;
                    color: #999;
                    margin-bottom: 8px;
                    display: flex;
                    align-items: center;

                    i {
                        margin-right: 4px;
                    }
                }

                .pet-vaccine {
                    font-size: 12px;
                    padding: 2px 8px;
                    border-radius: 4px;
                    display: inline-block;
                    background-color: #f0f0f0;
                    color: #666;

                    &.vaccined {
                        background-color: #e1f3d8;
                        color: #1a933e;
                    }
                }
            }
        }
    }
}

/* 列表动画效果 */
.list-enter-active,
.list-leave-active {
    transition: all 0.5s ease;
}

.list-enter,
.list-leave-to {
    opacity: 0;
    transform: translateY(30px);
}

.list-move {
    transition: transform 0.5s ease;
}

/* 新增样式：置顶板块样式 */
.top-post-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(64, 158, 255, 0.2);
    transition: all 0.3s ease;
}

/* 响应式调整 */
@media (max-width: 1200px) {
    .top-post-item {
        width: calc(50% - 20px) !important;
    }
}

@media (max-width: 768px) {
    .top-post-item {
        width: 100% !important;
    }
}

/* 响应式调整 */
@media (max-width: 1200px) {
    .pet-list .pet-card {
        width: calc((100% - 80px) / 5);
    }
}

@media (max-width: 992px) {
    .pet-list .pet-card {
        width: calc((100% - 60px) / 4);
    }
}

@media (max-width: 768px) {
    .pet-list .pet-card {
        width: calc((100% - 40px) / 3);
    }
}

@media (max-width: 576px) {
    .pet-list .pet-card {
        width: calc((100% - 20px) / 2);
    }
}
</style>