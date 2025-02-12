// Chart 1: 각 물품 별 진행 단계 차트(Horizontal Bar Chart)
const ctx1 = document.getElementById('chart1').getContext('2d');
new Chart(ctx1, {
	type: 'bar',
	data: {
		labels: ['물품 A', '물품 B', '물품 C', '물품 D'],
		datasets: [{
			label: '진행 단계',
			data: [3, 1, 4, 2],
			backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
		}]
	},
	options: {
		indexAxis: 'y',
		scales: {
			x: {
				beginAtZero: true,
				max: 5 // 총 단계 수에 따라 조정
			}
		},
		plugins: {
			title: {
				display: true,
				text: '각 물품 별 진행 단계',
				align: 'start',
				position: 'top',
				font: {
					size: 24,
					weight: 'bold'
				},
				padding: {
					top: 10,
					left: 10
				}
			}
		}
	}
});

// Chart 2: 날짜 별 박스 파손률 차트(Line Chart)
const ctx2 = document.getElementById('chart2').getContext('2d');
new Chart(ctx2, {
	type: 'line',
	data: {
		labels: ['2025-02-01', '2025-02-02', '2025-02-03', '2025-02-04'],
		datasets: [{
			label: '박스 파손률',
			data: [2.5, 3.1, 2.8, 3.4],
			borderColor: 'rgb(75, 192, 192)',
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
				font: {
					size: 24,
					weight: 'bold'
				},
				padding: {
					top: 10,
					left: 10
				}
			}
		}
	}
});

// Chart 3: 날짜 별 물품 주문 건수 차트(Bar Chart)
const ctx3 = document.getElementById('chart3').getContext('2d');
new Chart(ctx3, {
	type: 'bar',
	data: {
		labels: ['2025-01-29', '2025-01-30', '2025-01-31', '2025-02-01', '2025-02-02', '2025-02-03', '2025-02-04'],
		datasets: [{
			label: '물품 주문 건수',
			data: [12, 19, 15, 25, 22, 30, 28],
			backgroundColor: 'rgba(75, 192, 192, 0.6)',  // 막대 색상 설정
			borderColor: 'rgba(75, 192, 192, 1)',
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
					text: '물품 주문 건수'
				}
			}
		},
		plugins: {
			title: {
				display: true,
				text: '날짜 별 물품 주문 건수',
				align: 'start',
				position: 'top',
				font: {
					size: 24,
					weight: 'bold'
				},
				padding: {
					top: 10,
					left: 10
				}
			}
		}
	}
});

// Chart 4: 캠프 별 상품 유동량 차트(Choropleth Chart)
async function createSeoulMap() {
	const seoul = await fetch('/json/seoul_HangJeongDong.json').then((r) => r.json());

	const nation = ChartGeo.topojson.feature(seoul, seoul.objects.seoul_HangJeongDong).features[0];
	const states = ChartGeo.topojson.feature(seoul, seoul.objects.seoul_HangJeongDong).features;
	
	const ctx4 = document.getElementById('chart4').getContext('2d');
	new Chart(ctx4, {
		type: 'choropleth',
		data: {
			labels: states.map(d => d.properties.adm_nm),
			datasets: [{
				label: '서울시 행정동',
				outline: nation,
				data: states.map(d => ({
					feature: d,
					value: (function() {
					        switch (d.properties.sggnm) {
					            case '송파구':
					                return 200;
								case '서초구':
								    return 20;
								case '강남구':
								    return 40;
								case '강서구':
								    return 60;
								case '중구':
								    return 80;
								case '성동구':
								    return 100;
					            default:
					                return 0;
					        }
					    }
					)()
				}))
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
					text: '캠프 별 상품 유동량',
					align: 'start',
					position: 'top',
					font: {
						size: 24,
						weight: 'bold'
					},
					padding: {
						top: 10,
						left: 10
					}
				}
			},
			scales: {
				projection: {
					axis: 'x',
					projection: 'mercator',
					center: [126.986, 37.565],
					padding: 153
				}
			}
		}
	});
}

createSeoulMap();

// Chart 5: 박스 종류 별 사용량 차트(Pie Chart)
const ctx5 = document.getElementById('chart5').getContext('2d');
new Chart(ctx5, {
	type: 'pie',
	data: {
		labels: ['1호', '2호', '3호', '4호', '5호'],
		datasets: [{
			data: [30, 25, 20, 15, 10],
			backgroundColor: [
				'#FF6384',
				'#36A2EB',
				'#FFCE56',
				'#4BC0C0',
				'#9966FF'
			]
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
				font: {
					size: 24,
					weight: 'bold'
				},
				padding: {
					top: 10,
					left: 10
				}
			}
		}
	}
});

// Chart 6: 물품 카테고리 별 취급주의 물품 비율 차트(Polar Area Chart)
const ctx6 = document.getElementById('chart6').getContext('2d');
new Chart(ctx6, {
	type: 'polarArea',
	data: {
		labels: ['A', 'B', 'C', 'D', 'E'],
		datasets: [{
			data: [30, 25, 20, 15, 10],
			backgroundColor: ['red', 'blue', 'green', 'yellow', 'purple']
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
				font: {
					size: 24,
					weight: 'bold'
				},
				padding: {
					top: 10,
					left: 10
				}
			}
		}
	}
});