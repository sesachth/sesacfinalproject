// ✅ 보라색 계열 색상 팔레트 (투명도 추가)
const purpleColors = [
    'rgba(104, 38, 158, 0.7)',  // #68269E → 70% 투명
    'rgba(217, 170, 255, 0.7)', // #D9AAFF
    'rgba(213, 191, 231, 0.7)', // #D5BFE7
    'rgba(181, 137, 218, 0.7)', // #B589DA
    'rgba(177, 162, 190, 0.7)', // #B1A2BE
    'rgba(236, 218, 250, 0.7)'  // #ECDAFA
];

const purpleColors2 = [
    'rgba(163, 51, 255, 0.7)',  // #A333FF
    'rgba(133, 0, 241, 0.7)',   // #8500F1
    'rgba(167, 124, 241, 0.7)', // #A77CF1
    'rgba(194, 183, 242, 0.7)', // #C2B7F2
    'rgba(188, 150, 255, 0.7)', // #BC96FF
    'rgba(96, 53, 194, 0.7)'    // #6035C2
];

// 공통 폰트 설정
const customFont = {
    family: '"Poppins", "Inter", "Noto Sans KR", sans-serif',
    size: 22,  // ✅ 폰트 크기 키움
    weight: 600,  // ✅ 강조
    lineHeight: 1.4
};

// Chart 1: 진행 단계 별 물품 개수 차트(Horizontal Bar Chart)
const ctx1 = document.getElementById('chart1').getContext('2d');
// Chart 2: 날짜 별 박스 파손률 차트(Line Chart)
const ctx2 = document.getElementById('chart2').getContext('2d');
// Chart 3: 날짜 별 주문 건수 차트(Bar Chart)
const ctx3 = document.getElementById('chart3').getContext('2d');
// Chart 4: 캠프 별 물품 유동량 차트(Choropleth Chart)
const ctx4 = document.getElementById('chart4').getContext('2d');
// Chart 5: 박스 종류 별 사용량 차트(Pie Chart)
const ctx5 = document.getElementById('chart5').getContext('2d');
// Chart 6: 물품 카테고리 별 취급주의 물품 비율 차트(Polar Area Chart)
const ctx6 = document.getElementById('chart6').getContext('2d');

// Ajax 요청을 보내고 차트를 그리는 함수
function fetchDataAndCreateChart1() {
    $.ajax({
        url: '/api/dashboard/chart1Data',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            // 데이터 처리
            const labels = [];
            const data = [];
            response.forEach(item => {
                labels.push(getProgressStateLabel(item.progress_state));
                data.push(item.count);
            });

            // 차트 생성
            new Chart(ctx1, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '물품 개수',
                        data: data,
                        backgroundColor: purpleColors,
                        borderColor: purpleColors.map(color => color.replace('A9', 'FF')),
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y',
                    scales: {
                        x: {
                            beginAtZero: true,
                            max: Math.max(...data) + 1 // 데이터의 최대값에 따라 동적으로 조정
                        }
                    },
                    plugins: {
                        title: {
                            display: true,
                            text: '진행 단계 별 물품 개수',
                            align: 'start',
                            position: 'top',
                            font: customFont,
                            padding: {
                                top: -4,
                                left: 0
                            }
                        }
                    }
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
}

// Ajax 요청을 보내고 차트를 그리는 함수
function fetchDataAndCreateChart2() {
    $.ajax({
        url: '/api/dashboard/chart2Data',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            // 데이터 처리
            const labels = [];
            const data = [];
            response.forEach(item => {
                labels.push(item.date);
                data.push(parseFloat(item.damage_rate).toFixed(2));
            });

            // 차트 생성
            new Chart(ctx2, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '박스 파손률',
                        data: data,
                        borderColor: purpleColors[0],
                        backgroundColor: purpleColors2[2],
                        fill: true,
                        tension: 0.1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: '파손률 (%)'
                            }
                        },
                        x: {
                            title: {
                                display: true,
                                text: '날짜'
                            }
                        }
                    },
                    plugins: {
                        title: {
                            display: true,
                            text: '날짜 별 박스 파손률',
                            align: 'start',
                            position: 'top',
                            font: customFont,
                            padding: {
                                top: -4,
                                left: 0
                            }
                        }
                    }
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
}

// Ajax 요청을 보내고 차트를 그리는 함수
function fetchDataAndCreateChart3() {
    $.ajax({
        url: '/api/dashboard/chart3Data',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            // 데이터 처리
            const labels = [];
            const data = [];
            response.forEach(item => {
                labels.push(item.date);
                data.push(item.order_count);
            });

            // 차트 생성
            new Chart(ctx3, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: '주문 건수',
                        data: data,
                        backgroundColor: purpleColors,
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        x: {
                            type: 'time',
                            time: {
                                unit: 'day',
                                displayFormats: {
                                    day: 'yyyy년 MM월 dd일'
                                }
                            },
                            ticks: {
                                callback: function(value, index, values) {
                                    const date = new Date(value);
                                    return new Intl.DateTimeFormat('ko-KR', {
                                        year: 'numeric',
                                        month: 'long',
                                        day: 'numeric'
                                    }).format(date);
                                }
                            },
                            title: {
                                display: true,
                                text: '날짜'
                            }
                        },
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: '주문 건수'
                            }
                        }
                    },
                    plugins: {
                        title: {
                            display: true,
                            text: '날짜 별 주문 건수',
                            align: 'start',
                            position: 'top',
                            font: customFont,
                            padding: {
                                top: -4,
                                left: 0
                            }
                        }
                    }
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
}

async function fetchDataAndCreateChart4() {
    const seoul = await fetch('https://sesac-project-bucket.s3.ap-northeast-2.amazonaws.com/json/seoul_HangJeongDong.json').then((r) => r.json());
    const nation = ChartGeo.topojson.feature(seoul, seoul.objects.seoul_HangJeongDong).features[0];
    const states = ChartGeo.topojson.feature(seoul, seoul.objects.seoul_HangJeongDong).features;

    const response = await fetch('/api/dashboard/chart4Data');
    const destinationData = await response.json();

    const destinationCountMap = {};
    destinationData.forEach(item => {
        destinationCountMap[item.destination] = item.count;
    });

    const colors = ['#3E1F58', '#572E79', '#68269E', '#C8A1E0', '#E2BFD9'];
    const sggnmColorMap = {};
    let colorIndex = 0;

    function getColorForSggnm(sggnm) {
        if (!sggnmColorMap[sggnm]) {
            sggnmColorMap[sggnm] = colors[colorIndex % colors.length];
            colorIndex++;
        }
        return sggnmColorMap[sggnm];
    }

    function getValue(feature) {
        const sggnm = feature.properties.sggnm;
        return destinationCountMap[sggnm] || 0;
    }

    new Chart(ctx4, {
        type: 'choropleth',
        data: {
            labels: states.map(d => d.properties.adm_nm),
            datasets: [{
                label: '서울시 행정동',
                backgroundColor: purpleColors[2],
                outline: nation,
                data: states.map(d => {
                    const value = getValue({ properties: { sggnm: d.properties.sggnm } });
                    return {
                        feature: d,
                        value: value,
                        color: value > 0 ? getColorForSggnm(d.properties.sggnm) : 'transparent'
                    };
                })
            }]
        },
        options: {
            showOutline: true,
            showGraticule: true,
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    display: true,
                    text: '캠프 별 물품 유동량',
                    align: 'start',
                    position: 'top',
                    font: customFont,
                    padding: {
                      top: -4,
                      left: 0
                    }
                },
            },
            scales: {
                projection: {
                    axis: 'x',
                    projection: 'mercator',
                    center: [126.986, 37.565],
                    padding: 122
                }
            },
            elements: {
                geoFeature: {
                    backgroundColor: context => context.dataIndex !== undefined ? context.dataset.data[context.dataIndex].color : 'transparent'
                }
            }
        }
    });
}


// Ajax 요청을 보내고 차트를 그리는 함수
function fetchDataAndCreateChart5() {
    $.ajax({
        url: '/api/dashboard/chart5Data',
        method: 'GET',
        dataType: 'json',
        success: function(response) {
            // 데이터 처리
            const labels = [];
            const data = [];
            const backgroundColors = ['#A333FF', '#8500F1', '#A77CF1', '#C2B7F2', '#BC96FF', '#6035C2'];

            response.forEach((item, index) => {
                labels.push(`${item.spec}호`);
                data.push(item.count);
            });

            // 차트 생성
            new Chart(ctx5, {
                type: 'pie',
                data: {
                    labels: labels,
                    datasets: [{
                        data: data,
                        backgroundColor: purpleColors
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: '박스 종류 별 사용량',
                            align: 'start',
                            position: 'top',
                            font: customFont,
                            padding: {
                                top: -4,
                                left: 0
                            }
                        }
                    }
                }
            });
        },
        error: function(xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
}

// progress_state 값을 라벨로 변환하는 함수
function getProgressStateLabel(state) {
    switch(state) {
        case 0: return '물품 대기';
        case 1: return '포장 완료';
        case 2: return '적재 완료';
        // 필요에 따라 더 많은 상태를 추가할 수 있습니다.
        default: return `상태 ${state}`;
    }
}

// 페이지 로드 시 차트 생성 함수 호출
$(document).ready(function() {
    fetchDataAndCreateChart1();
	fetchDataAndCreateChart2();
	fetchDataAndCreateChart3();
	fetchDataAndCreateChart4();
	fetchDataAndCreateChart5();
});

new Chart(ctx6, {
	type: 'polarArea',
	data: {
		labels: ['A', 'B', 'C', 'D', 'E'],
		datasets: [{
			data: [30, 25, 20, 15, 10],
			backgroundColor: purpleColors2
		}]
	},
	options: {
		responsive: true,
		plugins: {
			title: {
				display: true,
				text: '물품 카테고리 별 취급주의 물품 비율',
				align: 'start',
				position: 'top',
				font: customFont,
				padding: {
					top: -4,
					left: 0
				}
			}
		}
	}
});