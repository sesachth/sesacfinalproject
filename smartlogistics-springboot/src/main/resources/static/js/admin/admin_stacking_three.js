//import * as THREE from 'https://cdn.jsdelivr.net/npm/three@0.173.0/build/three.module.js';

let stackingResults;

$(document).ready(function() {
	$.ajax({
        url: 'http://localhost:8000/api/v1/stacking_results',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
			stackingResults = response;
			
			// 전체 JSON 데이터 출력
			console.log("전체 데이터:", stackingResults);

			// pallets 배열 순회
			stackingResults.pallets.forEach((pallet, palletIndex) => {
			    console.log(`\n팔레트 ${palletIndex + 1}:`);
			    console.log("목적지:", pallet.destination);

			    // boxes 배열 순회
			    pallet.boxes.forEach((box, boxIndex) => {
			        console.log(`\n  박스 ${boxIndex + 1}:`);
			        console.log("    제품명:", box.product_name);
			        console.log("    너비:", box.width);
			        console.log("    깊이:", box.depth);
			        console.log("    높이:", box.height);
			        console.log("    X 좌표:", box.x_coordinate);
			        console.log("    Y 좌표:", box.y_coordinate);
			        console.log("    Z 좌표:", box.z_coordinate);
			    });
			});
			
			//animateStackingResults();
        },
        error: function(xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
});

/*
function animateStackingResults() {
	// 씬 생성
	const scene = new THREE.Scene();

	// 카메라 설정
	const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
	camera.position.set(3, 3, 5);
	camera.lookAt(0, 0, 0);

	// 렌더러 생성
	const renderer = new THREE.WebGLRenderer();
	renderer.setSize(window.innerWidth / 2, window.innerHeight / 2);
	document.querySelector('.main-content').appendChild(renderer.domElement);

	// 큐브 생성 (물체)
	const geometry = new THREE.BoxGeometry(1.1, 0.15, 1.1); // 크기를 1.1, 0.15, 1.1로 설정
	const material = new THREE.MeshPhongMaterial({ color: 0x808080 }); // 색상을 회색(0x808080)으로 설정
	const cube = new THREE.Mesh(geometry, material);

	// 큐브 위치 조정: 모서리를 (0, 0, 0)에 맞추기 위해 이동
	cube.position.set(1.1 / 2, 0.15 / 2, 1.1 / 2); // x축: +0.55, y축: +0.075, z축: +0.55
	scene.add(cube);

	// 조명 추가 및 조정
	const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
	scene.add(ambientLight);

	const pointLight = new THREE.PointLight(0xffffff, 1);
	pointLight.position.set(5, 5, 5);
	scene.add(pointLight);

	// AxesHelper 추가 (좌표계 표시)
	const axesHelper = new THREE.AxesHelper(5); // 축의 길이를 5로 설정
	scene.add(axesHelper);

	// 회전을 위한 변수 초기화
	let isDragging = false;
	let previousMousePosition = { x: 0, y: 0 };
	let rotationSpeed = 0.005;

	// 줌을 위한 변수 초기화
	let zoomSpeed = 0.1;
	let minDistance = 1;
	let maxDistance = 20;

	// 마우스 이벤트 핸들러
	document.addEventListener('mousedown', (event) => {
	    isDragging = true;
	});

	document.addEventListener('mouseup', (event) => {
	    isDragging = false;
	});

	document.addEventListener('mousemove', (event) => {
	    if (isDragging) {
	        const deltaX = event.clientX - previousMousePosition.x;
	        const deltaY = event.clientY - previousMousePosition.y;

	        const spherical = new THREE.Spherical();
	        const cameraPosition = new THREE.Vector3();
	        cameraPosition.copy(camera.position).sub(cube.position);
	        spherical.setFromVector3(cameraPosition);

	        spherical.theta -= deltaX * rotationSpeed;
	        spherical.phi -= deltaY * rotationSpeed;

	        spherical.phi = Math.max(0.1, Math.min(Math.PI - 0.1, spherical.phi));

	        cameraPosition.setFromSpherical(spherical).add(cube.position);
	        camera.position.copy(cameraPosition);
	        camera.lookAt(cube.position);
	    }

	    previousMousePosition.x = event.clientX;
	    previousMousePosition.y = event.clientY;
	});

	// 마우스 휠 이벤트 핸들러 (줌인/아웃)
	document.addEventListener(
	    'wheel',
	    (event) => {
	        event.preventDefault();

	        const zoomAmount = event.deltaY * zoomSpeed;
	        const cameraPosition = new THREE.Vector3();
	        cameraPosition.copy(camera.position).sub(cube.position);

	        // 줌 인/아웃 제한 설정
	        if (
	            (zoomAmount > 0 && cameraPosition.length() < maxDistance) ||
	            (zoomAmount < 0 && cameraPosition.length() > minDistance)
	        ) {
	            cameraPosition.multiplyScalar(1 + zoomAmount / 100);
	        }

	        camera.position.copy(cameraPosition.add(cube.position));
	        camera.lookAt(cube.position);
	    },
	    { passive: false }
	);

	// 애니메이션 루프
	function animate() {
	    requestAnimationFrame(animate);
	    renderer.render(scene, camera);
	}
	animate();

	// 창 크기 변경 대응
	window.addEventListener('resize', () => {
	    camera.aspect = window.innerWidth / window.innerHeight;
	    camera.updateProjectionMatrix();
	    renderer.setSize(window.innerWidth, window.innerHeight);
	});	
}
*/