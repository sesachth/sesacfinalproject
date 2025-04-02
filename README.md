# SMART LOGISTICS

프로젝트 개요
---

+ 프로젝트명: SmartLogistics
+ 팀명: Boxer
+ 팀 인원: 총 5명
+ 개발 기간: 2025.01.16 ~ 2025.02.25
+ 프로젝트 목표: 온라인 쇼핑이 급증하면서, 이에 따른 고객 불만들이 발생하고 있습니다. 이 중에서 **과대 포장 및 택배 박스 손상 문제**를 해결하기 위해, 아래 **3가지 솔루션을 웹/인공지능 기반 프로젝트로 구현**하였습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/b5873e7d-5ce2-489d-9612-497f5def0b79">
</p>

주요 기능
---

관리자 전용 웹페이지와, 작업자 전용 웹페이지로 나눠 다음처럼 기능들을 구현하였습니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/0a768525-9acb-43f5-9152-8ed8917c7323">
</p>

프로젝트 참고 자료
---

[발표 PPT (pdf)](https://drive.google.com/file/d/1TC_pg93pJdySG4qOx3W-4WVm9ICIoNgA/view?usp=sharing)

[시연 동영상 (mp4)](https://drive.google.com/file/d/1agk7A77UzePiDsSBHUwagg17O1_dgyni/view?usp=sharing)

[프로젝트 정보 (Notion)](https://www.notion.so/Boxer-1a58fb50803c80f39f52c1d13df91642?pvs=4)

기술 스택
---

| 분류          | 배지 |
|---------------|------|
| __Frontend__      | ![Ajax](https://img.shields.io/badge/Ajax-005571?style=for-the-badge&logo=ajax&logoColor=white) ![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=for-the-badge&logo=bootstrap&logoColor=white) ![Chart.js](https://img.shields.io/badge/Chart.js-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white) ![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white) ![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white) ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black) ![jQuery](https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jQuery&logoColor=white) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white) ![Three.js](https://img.shields.io/badge/Three.js-000000?style=for-the-badge&logo=three.js&logoColor=white) |
| __Backend__       | ![Apache POI](https://img.shields.io/badge/Apache%20POI-D22128?style=for-the-badge&logo=apache&logoColor=white) ![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-BF1A1A?style=for-the-badge&logo=mybatis&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![WebSocket](https://img.shields.io/badge/WebSocket-000000?style=for-the-badge&logo=websocket&logoColor=white) |
| __Storage Cloud__ | ![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazonaws&logoColor=white) ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazonaws&logoColor=white) |
| __AI__            | ![TensorFlow](https://img.shields.io/badge/TensorFlow-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white) ![OpenCV](https://img.shields.io/badge/OpenCV-27338e?style=for-the-badge&logo=OpenCV&logoColor=white) ![Keras](https://img.shields.io/badge/Keras-FF0000?style=for-the-badge&logo=keras&logoColor=white) |

설치 및 실행 방법
---

__1. 프로젝트 클론__

먼저, 이 레포지토리를 로컬 환경으로 클론합니다:

    git clone https://github.com/sesachth/sesacfinalproject.git

    cd sesacfinalproject

__2. Spring Boot 서버 실행__

2.1. 요구사항

+ Java 17 이상

+ Gradle 또는 Maven

+ MySQL (또는 설정된 데이터베이스)

2.2. 데이터베이스 설정

__application.properties__ 파일에서 데이터베이스 정보를 설정합니다:

    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/your-database
          username: your-username
          password: your-password

데이터베이스를 생성합니다:

    CREATE DATABASE your-database;

2.3. 의존성 설치 및 빌드

Gradle을 사용하여 의존성을 설치하고 프로젝트를 빌드합니다:

    ./gradlew build

2.4. Spring Boot 애플리케이션 실행

Spring Boot 애플리케이션을 실행합니다:

    java -jar build/libs/your-project-name.jar
   
Spring Boot 서버가 기본적으로 http://localhost:8080 에서 실행됩니다.

__3. FastAPI 서버 실행__

3.1. 요구사항

+ Python 3.8 이상

+ 가상환경(Virtual Environment) 활성화 권장

3.2. 가상환경 생성 및 활성화
    
+ 가상환경 생성:

        python -m venv venv
        
+ 가상환경 활성화:

    Windows:

        .\venv\Scripts\activate
        
    macOS/Linux:

        source venv/bin/activate
        
3.3. 의존성 설치
    
필요한 Python 패키지를 설치합니다:

        pip install -r requirements.txt
        
3.4. FastAPI 서버 실행
    
FastAPI 서버를 실행합니다:

        uvicorn main:app --reload
        
FastAPI 서버가 기본적으로 http://127.0.0.1:8000 에서 실행됩니다.

__4. API 테스트__

+ Spring Boot API 테스트:

    Postman 또는 cURL을 사용하여 http://localhost:8080/api 경로를 테스트합니다.

+ FastAPI API 문서 확인:

    브라우저에서 http://127.0.0.1:8000/docs 로 이동하여 Swagger UI를 통해 API를 테스트할 수 있습니다.

__5. 환경 변수 설정__

.env 파일을 생성하고 필요한 값을 설정합니다:

__6. 프로젝트 종료__

서버를 종료하려면 터미널에서 Ctrl + C를 누릅니다.

__이 단계를 따라하면 SMART LOGISTICS 프로젝트를 성공적으로 설치하고 실행할 수 있습니다!__

아키텍처 및 디렉터리 구조
---

<p align="center">
  <img src="https://github.com/user-attachments/assets/24ba9013-980a-47e9-9e51-9e64aed085ac">
</p>

SpringBoot 프로젝트 디렉터리 구조입니다.

    smartlogistics-springboot
    ├─gradle
    │  └─wrapper                    # 프로젝트의 의존성 관리
    └─src
        └─main
           ├─java
           │  └─app
           │      └─labs
           │          ├─config      # 보안 설정(Spring Security), CORS 설정
           │          ├─controller  # 클라이언트 요청을 처리, RESTful API 엔드포인트 정의
           │          ├─dao         # MyBatis를 사용하여 데이터베이스 CRUD 작업을 수행
           │          ├─model       # 데이터베이스 테이블과 매핑
           │          ├─service     # 비즈니스 로직을 처리
           │          └─websocket   # WebSocket 기반 실시간 데이터 전송 구현
           └─resources
               ├─mybatis
               │  └─mappers         # XML 파일 기반 SQL 쿼리 정의
               ├─static
               │  ├─css
               │  │  ├─admin        # 관리자 웹페이지 측 CSS 파일 관리
               │  │  └─worker       # 작업자 웹페이지 측 CSS 파일 관리
               │  ├─images
               │  │  ├─admin        # 관리자 웹페이지 측 이미지 파일 관리
               │  │  └─worker       # 작업자 웹페이지 측 이미지 파일 관리
               │  └─js
               │      ├─admin       # 관리자 웹페이지 측 JavaScript 파일 관리
               │      └─worker      # 작업자 웹페이지 측 JavaScript 파일 관리
               └─templates
                   └─thymeleaf
                       └─html
                           ├─admin  # 관리자 웹페이지 측 HTML 파일 관리
                           ├─common # 기본 레이아웃, 로그인 웹페이지 HTML 파일 관리
                           └─worker # 작업자 웹페이지 측 HTML 파일 관리


FastAPI 프로젝트 디렉터리 구조입니다.

    smartlogistics-fastapi
    ├─api
    │  └─v1
    │      └─endpoints  # 라우팅 엔드포인트 정의
    ├─core
    │  ├─db             # 데이터베이스 연결 및 설정
    │  └─helpers        # 알고리즘, 이미지 기반 AI 모델 처리 로직
    ├─models            # 데이터베이스 모델 정의
    └─tests             # 테스트 코드


API 문서
---

Spring Boot 프로젝트 측 API 문서입니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/d36fde5a-b398-40b9-8649-28c75e8d975a">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/55ed60df-968c-4e39-9823-a44b99cf4558">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/c3f23e92-d3c5-483a-9986-d3c68e40734b">
</p>

FastAPI 프로젝트 측 API 문서입니다.

<p align="center">
  <img src="https://github.com/user-attachments/assets/132d424e-5409-48cd-9610-c3f1c067afab">
</p>

팀원 정보
---

<p align="center">
  <img src="https://github.com/user-attachments/assets/d41c41aa-9c43-43a9-8537-4802f0ae0852">
</p>
