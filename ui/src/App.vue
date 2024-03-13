<template>
  <div class="max">
    <n-modal v-model:show="showModal" style="border-radius: 6px;margin: auto;">
      <div style="box-sizing: border-box;overflow: hidden;">
        <div style="width: 100%;display: flex;justify-content: center;">
          <video id="video" autoplay controls :src='videoUrl' width="900px" height="500px"></video>
        </div>
        <div style="margin-top: 10px;width: 900px;">
          <h2 style="color: white;">{{ item['title'] }}</h2>
          <p style="color: white;">{{ item['description'] }}</p>
          <p @click="toOpen(`https://steamcommunity.com/sharedfiles/filedetails/?id=${item.id}`)"
             style="text-decoration: underline;cursor: pointer; color: white;">创意工坊</p>
        </div>
      </div>
    </n-modal>
    <div id="content" style="margin: auto;height: 100%;box-sizing: border-box;">
      <div style="display: flex;flex-flow: column;height:100%;">
        <div style="flex-grow: 1;display: flex; align-items: center;justify-content: center;">
          <n-space>
            <n-input id="input" style="width: 400px;" v-model:value="searchText" type="text" placeholder="请输入关键字"
                     @keyup.enter="search(1)"/>
            <n-button style="cursor: pointer;" @click="search(1)">搜索</n-button>
            <n-button @click="loadList">
              <template #icon>
                <n-icon>
                  <ReloadCircleSharp/>
                </n-icon>
              </template>
            </n-button>
          </n-space>
        </div>
        <div id="list" style="overflow: hidden;flex-grow: 7;margin-bottom: 10px;box-sizing: border-box;">
          <n-scrollbar style="overflow:hidden;width: 100%;">
            <div
                id="list-content"
                style="display: grid;
                gap: 5px 5px;
                ">
              <div class="light-green click"
                   @click="check(item)"
                   v-for="item in list"
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
          <n-pagination
              :onUpdatePage="search"
              :onUpdatePageSize="updatePageSize"
              v-model:page="pageNo"
              v-model:page-count="totalPage"
              :page-sizes="[100, 200, 300, 400,500]"
              v-model:page-size="pageSize"
              show-quick-jumper
              show-size-picker/>
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
  cursor: pointer;
}

.two-lines {
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.click:hover {
  border: 4px solid black;
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
import {ReloadCircleSharp} from "@vicons/ionicons5"

let list = ref([]);
let pageNo = ref(1);
let totalPage = ref(1);
let pageSize = ref(100);

let videoUrl = ref('');
let showModal = ref(false);
let searchText = ref('');

let item = ref({});


onMounted(() => {
  let size = localStorage.getItem('size');
  if (size) {
    pageSize.value = Number.parseInt(size);
  }
  search();
})

let search = (_pageNo) => {
  if (_pageNo) {
    pageNo.value = _pageNo
  }
  fetch('/api/list', {
    method: 'POST',
    body: JSON.stringify(
        {
          text: searchText.value,
          pageNo: pageNo.value,
          size: pageSize.value
        }
    )
  })
      .then(req => req.json())
      .then(json => {
        totalPage.value = json['totalPage']
        list.value = json['list']
      })
}

let loadList = () => {
  fetch('/api/loadList', {
    method: 'POST'
  })
}

let toOpen = url => open(url)

let updatePageSize = (size) => {
  localStorage.setItem('size', size);
}

let check = (_item) => {
  item.value = _item
  videoUrl.value = `/api/play?id=${_item.id}`;
  showModal.value = true
}

</script>
