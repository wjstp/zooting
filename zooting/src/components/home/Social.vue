<template>
  <div class="social__container">
    <Search 
      @search-state="searchState"
      @search-query="handleSearchQuery"
    />
    <SearchResult
    v-show="isSearching"
    :search-query="searchQuery"
    />
    <SocialTabBar 
      v-show="!isSearching"
      :tabs="tabs"
      @select-tab="handleTabSelected"
    />
    <component
      v-show="!isSearching"
      :is="currentList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, shallowRef, onMounted, computed } from 'vue'
import { useAccessTokenStore } from '@/stores/store'
import Search from '@/components/home/Search.vue'
import SearchResult from '@/components/home/SearchResult.vue'
import SocialTabBar from '@/components/home/SocialTabBar.vue'
import SocialFriendList from '@/components/home/SocialFriendList.vue'
import SocialRequestList from '@/components/home/SocialRequestList.vue'
import SocialBlockList from '@/components/home/SocialBlockList.vue'

const store = useAccessTokenStore()
const tabs = ref<{name: string, count: any}[]>([
  { name: '친구', count: 0 },
  { name: '친구 요청', count: 0 },
  { name: '차단', count: 0 },
])
const isSearching = ref<boolean>(false)
const searchQuery = ref<string>('')

onMounted(async () => {
  store.getFriendList()
  store.getRequestFromList()
  store.getRequestToList()
  store.getBlockList()
})

tabs.value[0].count = computed(() => {
  return store.friendList?.length
})

tabs.value[1].count = computed(() => {
  return store.requestFromList?.length
})

tabs.value[2].count = computed(() => {
  return store.blockList?.length
})

const currentList = shallowRef<any>(SocialFriendList)

const handleTabSelected = (currentTab: string) => {
  if (currentTab === '친구') {
    currentList.value = SocialFriendList
  } else if (currentTab === '친구 요청') {
    currentList.value = SocialRequestList
  } else {
    currentList.value = SocialBlockList
  }
}

const searchState = (bool: boolean) => {
  isSearching.value = bool
}

const handleSearchQuery = (query: string) => {
  searchQuery.value = query
}
</script>

<style scoped>
.social__container {
  @apply flex flex-col h-screen bg-white border-r border-gray-200;
  min-width: 430px;
}
</style>