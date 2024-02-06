<template>
  <div class="max">
    <n-modal v-model:show="showModal" style="border-radius: 6px;margin: auto;">
      <video controls style="margin: 5px auto;" :src='videoUrl' width="900px" height="500px"></video>
    </n-modal>
    <div style="max-width: 80%;margin: auto;height: 100%;padding-top: 10%;box-sizing: border-box;">
      <div style="display: flex;flex-flow: column;height:100%;">
        <div style="flex-grow: 1;display: flex; align-items: center;justify-content: center;">
          <n-space>
            <n-input style="width: 400px;" v-model:value="searchText" type="text" placeholder="请输入关键字"
                     @keyup.enter="search"/>
            <n-button style="cursor: pointer;" @click="search">搜索</n-button>
          </n-space>
        </div>
        <div style="overflow: hidden;flex-grow: 7;margin-top: 40px;margin-bottom: 10px;box-sizing: border-box;">
          <n-scrollbar style="overflow:hidden;width: 100%;">
            <div
                style="display: grid;grid-template-columns: repeat(auto-fit,minmax(108px,1fr)); width: 100%;gap: 10px 10px;">
              <div class="light-green click"
                   @click="check(item.id)"
                   v-for="item in page.list"
                   :style="{
                     backgroundImage: `url(/api/preview?id=${item.id})`,
                     backgroundSize: 'cover'
                 }"
              >
                {{ item['title'] }}
              </div>
            </div>
          </n-scrollbar>
        </div>
        <n-space vertical style="margin: auto;padding-bottom: 40px;">
          <n-pagination v-model:page="page.pageNo" :page-count="page.totalPage"/>
        </n-space>
      </div>
    </div>
  </div>

</template>

<style scoped>
.light-green {
  height: 108px;
  width: 108px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.click {
  box-sizing: border-box;
  display: flex;
  align-items: end;
  font-size: 12px;
  color: white;
}

.click:hover {
  border: red solid 1px;
  cursor: pointer;
}

.max {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>

<script setup>

import {onMounted, ref} from 'vue'

let page = ref({
  pageNo: 0,
  size: 10,
  totalPage: 1,
  list: []
});
let videoUrl = ref('');
let showModal = ref(false);
let searchText = ref('');


onMounted(() => {
  search()
})

let search = () => {
  fetch('/api/list', {
    method: 'POST',
    body: JSON.stringify(
        {
          'text': searchText.value
        }
    )
  })
      .then(req => req.json())
      .then(json => {
        page.value = json;
      })
}

let check = (id) => {
  videoUrl.value = `/api/play?id=${id}`;
  showModal.value = true
}

</script>
