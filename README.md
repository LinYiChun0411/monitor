### 佈署方式
- 1 : 建立資料庫 
      執行 src/main/resources/sql/ai_database.sql     
- 2 : 修改資料庫連線參數(spring:datasource:)
      src/main/resources/application.yml 
- 3 : 修改檢測參數(inspector:check:)
      src/main/resources/application.yml 
- 4 : 修改mail參數-失敗才會寄送(inspector:alertmail:)
      src/main/resources/application.yml
- 5 : 修改scheduled更新頻率參數-每任務結束後起算(inspector:scheduled:)
      src/main/resources/application.yml     
- 6 : 執行 maven 打包指令產生的jar檔
- 7 : 瀏覽器輸入: http://localhost:8080/apiinspect/today 顯示狀態     
- 8 : 瀏覽器輸入: http://localhost:8080/actuator/shutdown 關閉程式
