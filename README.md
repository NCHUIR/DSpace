# DSpace CRIS 中興大學自製版

(forked from [Cineca/DSpace](https://github.com/Cineca/DSpace))

由 中興大學校史館組 張世澤<[zhshize@smail.nchu.edu.tw](mailto:zhshize@smail.nchu.edu.tw)> 開發/維護。

## 安裝

請服用[官方文件#Installation](https://wiki.lyrasis.org/display/DSPACECRIS/Installation)

## 客製化功能說明

### 修改社群列表(community-list)界面及加入全文統計功能
[d7c699d](https://github.com/NCHUIR/DSpace/commit/d7c699dd2f96cc44bef4834af263db538d535c9e)

![標記全文統計功能及介面變更的示意圖](doc/assets/community-list-fulltext-counter.png)

其中紅色框的部分爲*全文統計功能*，由舊版(
[commit](https://github.com/NCHUIR/DSpace/commit/03dd53c102a68e60ea71528039c624d3b9e4b48e)
)遷移修改而來。

### 在文件列表加入全文下載圖示
[a01b9e7](https://github.com/NCHUIR/DSpace/commit/a01b9e7f17ae5907e62dc1df390d18f9a6bf8a98), 
[c2dbf7a](https://github.com/NCHUIR/DSpace/commit/c2dbf7a5a8a815b5364e9f366aa67a3b705c72d8)

![加入全文下載圖示的文件列表的示意圖](doc/assets/item-list-fulltext-download-icon.png)

其中紅色框的部分爲*全文下載圖示功能*，如果沒有全文可供下載，將顯示"-"代表無法下載。

