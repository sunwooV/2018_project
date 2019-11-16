# school_project

■프로젝트명 : Open CV를 이용한 시각 장애인용 화폐 및 점자 자동 인식 프로그램

■작품명: 같이[가치]  -  2018년도 프로보노 공모전 인기상 수상

■작품 소개

 -본 어플리케이션의 명칭은 "같이[가치]”(이하 “같이”)로, 시각장애인과 ‘같이’ 하며 그렇기에 가치가 있다는 의미를 담고 있다. ‘같이’는 두 가지 기능을 수행한다. 첫 째, 점자 해독, 둘 째, 지폐를 인식하여 얼마인지 그 금액을 안내하는 기능 두 가지이다.
시각장애인은 음성서비스를 통해 ‘같이’를 실행하며 실행한 이후에는 스와이프를 통해 기능을 구별하여 사용할 수 있다. “같이”를 실행하는 동안에는 플래시가 항상 켜져 있으며, 점자해독기능의 경우 화면의 어느 곳이나 터치하여 점자 사진을 찍고, 지폐인식기능의 경우 화면에 지폐가 들어오는 즉시 얼마인지 그 금액을 안내한다. 


■작품 구성

[지폐 인식]

 1. 사용자가 스마트폰을 지폐에 댄다.
 
 2. 화면에 지폐가 들어오기 전까지 경고음이 울리고 들어오면 tensorflow-lite와 비교하여 음성 혹은 진동으로 알려준다.
 
 
[점자 인식]

 1. 사용자가 화면을 터치하여 점자 사진을 찍는다.
 
 2. 서버로 전송하여 분석 후 다시 사용자에게 전송하여 음성으로 알려준다.
 
 
 ■작품 기능
 
 ○ 점자해독기능
 
 스마트폰의 화면 중 어느 곳이나 터치하면 사진을 촬영할 수 있다. 언제 어디서 사진을 찍든 비슷한 상황, 빛의 밝기 등의 환경을 만들어 점자 해독의 정확성을 높이기 위해 플래시가 항상 켜져 있다. 점자를 촬영하면 서버에서 점자를 해독하고 그 내용을 스마트폰으로 재전송하여 해당 점자의 뜻을 음성 안내한다.
 
○ 지폐인식기능

 스마트폰 화면 안에 지폐가 들어올 때까지 일정 간격으로 경고음이 울린다. 시각장애인은 경고음이 울리지 않을 때까지 지폐를 이동하여 스마트폰에 지폐를 인식시킨다. 지폐를 인식하면 해당 금액이 얼마인지 음성 또는 진동으로 안내한다. 
 
 
 파일 설명 >>
 
 
 Together - 같이[가치] application 소스 파일 >> 지폐기능에서는 tensorflow-lite 사용 (android studio 사용)
 
 Server - application에서 컴퓨터(서버)로 사진을 넘길 때 컴퓨터(서버)가 사진을 받고 저장해주는 역할을 한다. (java 사용)
 
 imageProcess - application에서 넘긴 사진을 점자판 테두리를 기준으로 사진을 자른다. (python 사용)
 
 ImageTranslation - 점자판 테두리를 기준으로 잘린 점자사진을 처리하여 한글로 바꿔준다. (matlab 사용)
 


:: https://blog.naver.com/sws_221/221709902528
 
