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
                style="display: grid;
                width: 100%;
                gap: 10px 10px;
                grid-template-columns: repeat(auto-fill, 100px);
                grid-template-rows: 100px 100px;">

              <div class="light-green click"
                   @click="check(item.id)"
                   v-for="item in page.list"
                   :style="{
                     backgroundImage: `url(/api/preview?id=${item.id})`,
                     backgroundSize: 'cover'
                 }"
              >
                <p class="two-lines" style="margin:  0 3px;box-sizing: border-box;">
                  {{ item['title'] }}
                </p>
              </div>
            </div>
          </n-scrollbar>
        </div>
        <n-space vertical style="margin: auto;padding-bottom: 40px;">
          <n-pagination :onUpdatePage="search" v-model:page="page.pageNo" v-model:page-count="page.totalPage"/>
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
  font-size: 8px;
  color: white;
}

.two-lines {
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.click:hover {
  border: white solid 4px;
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
  pageNo: 1,
  size: 100,
  totalPage: 1,
  list: []
});
let videoUrl = ref('');
let showModal = ref(false);
let searchText = ref('');


onMounted(() => {
  search()
})

let search = (pageNo) => {
  if (pageNo) {
    page.value.pageNo = pageNo
  }
  fetch('/api/list', {
    method: 'POST',
    body: JSON.stringify(
        {
          text: searchText.value,
          pageNo: page.value.pageNo,
          size: 30
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
