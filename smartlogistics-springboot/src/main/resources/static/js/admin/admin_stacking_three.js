import * as THREE from 'three';  // Three.js 라이브러리 가져오기

let scene, camera, renderer, baseCube, boxes = [];
let isDragging = false;
let previousMousePosition = { x: 0, y: 0 };
let rotationSpeed = 0.005;
let zoomSpeed = 0.1;
let minDistance = 1;
let maxDistance = 20;
let stackingResults = null;

$(document).ready(function() {
    // Ajax 요청으로 stackingResults 데이터 가져오기
    $.ajax({
        url: '/api/stacking/stackingResults', // RESTFul API 엔드포인트
        method: 'GET',
        dataType: 'json',
        success: function(response) {
			stackingResults = response;
            populatePalletSelect(stackingResults.pallets);
        },
        error: function(xhr, status, error) {
            console.error('Error fetching stacking results:', error);
            alert('Failed to load stacking results data.');
        }
    });

    $('#palletSelect').change(function() {
        const selectedPalletId = $(this).val();
        if (selectedPalletId) {
			updateBoxList(selectedPalletId);
            initThreeJS(selectedPalletId);
        }
    });
});

function populatePalletSelect(pallets) {
    const selectBox = $('#palletSelect');
    selectBox.empty();
    selectBox.append('<option value="">Select a pallet</option>');

    pallets.forEach(function(pallet) {
        selectBox.append(`<option value="${pallet.pallet_id}">${pallet.pallet_id}번 파레트 - ${pallet.destination}</option>`);
    });
}

function initThreeJS(palletId) {
    // // ✅ 기존 Three.js 캔버스 삭제 (새로 생성하지 않도록)
    // const oldCanvas = document.querySelector(".threejs-canvas");
    // if (oldCanvas) {
    //     oldCanvas.remove(); // 기존 Three.js 캔버스 제거
    // }

    // ✅ Three.js 초기화
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xFFFFFF); // 완전 흰색 배경

    camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    camera.position.set(2, 3, 3);
    camera.lookAt(0, 0, 0);

    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth / 2, window.innerHeight / 2);
    $('.main-content').append(renderer.domElement);

    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;

    


    // ✅ 바닥 추가
    const floorGeometry = new THREE.PlaneGeometry(5, 5);
    const floorMaterial = new THREE.MeshBasicMaterial({ color: 0xFFFFFF });
    const floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.rotation.x = -Math.PI / 2;
    floor.position.set(0, 0, 0);
    scene.add(floor);

    // ✅ 팔레트 추가
    const baseGeometry = new THREE.BoxGeometry(1.1, 0.15, 1.1);
    const baseMaterial = new THREE.MeshPhongMaterial({ color: 0x8B5A2B });
    baseCube = new THREE.Mesh(baseGeometry, baseMaterial);
    baseCube.position.set(0, 0.075, 0);
    scene.add(baseCube);

    // ✅ 조명 설정
    const ambientLight = new THREE.AmbientLight(0xffffff, 1.5);
    scene.add(ambientLight);

    const mainLight = new THREE.DirectionalLight(0xffffff, 2.0);
    mainLight.position.set(5, 10, 5);
    scene.add(mainLight);

    renderer.toneMapping = THREE.ACESFilmicToneMapping;
    renderer.toneMappingExposure = 1.5;

    // ✅ 기존 Three.js 화면을 초기화하고 새로운 팔레트의 박스를 배치
    if (stackingResults) {
        createBoxes(stackingResults.pallets[palletId].boxes);
        animate();
    } else {
        console.error("Error fetching box data:");
    }

    addEventListeners();
}







function updateBoxList(selectedPalletId) {
	const boxList = $('#boxList');
	boxList.empty();

	const selectedPalletData = stackingResults.pallets.find(p => p.pallet_id === parseInt(selectedPalletId));
	 
	if (selectedPalletData && selectedPalletData.boxes) {
	    selectedPalletData.boxes.forEach(box => {
	        const listItem = $(`
	            <div class="list-group-item">
	                <h5 class="mb-1">${box.product_name}</h5>
	            </div>
	        `);
	        
	        // 클릭 이벤트 리스너 추가
	        listItem.on('click', function() {
				highlightBox(box.product_name);
				selectBox($(this));
			});
	        
	        boxList.append(listItem);
	    });
	} else {
	    boxList.append('<div class="list-group-item">No boxes found for this pallet.</div>');
	}
}

function highlightBox(productName) {
  scene.traverse((object) => {
    if (object.userData && object.userData.productName === productName) {
      object.material.emissive.setHex(0xff0000);
      object.material.emissiveIntensity = 0.5;
    } else if (object.isMesh) {
      object.material.emissive.setHex(0x000000);
      object.material.emissiveIntensity = 0;
    }
  });
}

function selectBox($element) {
	// 모든 항목에서 'selected' 클래스 제거
	$('#boxList .list-group-item').removeClass('selected');
	// 클릭된 항목에 'selected' 클래스 추가
	$element.addClass('selected');
}

function createBoxes(boxesData) {
    boxes = [];
    const palletSize = 1.1; // ✅ 팔레트 크기 (고정)

    boxesData.forEach(boxData => {
        const boxGeometry = new THREE.BoxGeometry(boxData.width, boxData.height, boxData.depth);
        const boxMaterial = new THREE.MeshPhongMaterial({ color: Math.random() * 0xffffff });
        const boxMesh = new THREE.Mesh(boxGeometry, boxMaterial);

        // ✅ 팔레트의 중앙을 기준으로 박스 배치
        boxMesh.position.set(
            boxData.x_coordinate - (palletSize / 2) + (boxData.width / 2),  // ✅ 팔레트 중심 기준 좌표 변환
            0.15 + (boxData.height / 2),  // ✅ 팔레트 위에 정확히 배치
            boxData.z_coordinate - (palletSize / 2) + (boxData.depth / 2)   // ✅ 팔레트 중심 기준 좌표 변환
        );

        boxes.push({
            mesh: boxMesh,
            velocity: 0 // 정적 위치이므로 속도는 0
        });

        boxMesh.userData.productName = boxData.product_name;
        scene.add(boxMesh);
    });
}




function addEventListeners() {
    document.addEventListener('mousedown', onMouseDown);
    document.addEventListener('mouseup', onMouseUp);
    document.addEventListener('mousemove', onMouseMove);
    document.addEventListener('wheel', onWheel, { passive: false });
    window.addEventListener('resize', onWindowResize);
}

function onMouseDown(event) {
    isDragging = true;
}

function onMouseUp(event) {
    isDragging = false;
}

function onMouseMove(event) {
    if (isDragging) {
        const deltaX = event.clientX - previousMousePosition.x;
        const deltaY = event.clientY - previousMousePosition.y;

        const spherical = new THREE.Spherical();
        const cameraPosition = new THREE.Vector3();
        cameraPosition.copy(camera.position).sub(baseCube.position);
        spherical.setFromVector3(cameraPosition);

        spherical.theta -= deltaX * rotationSpeed;
        spherical.phi -= deltaY * rotationSpeed;

        spherical.phi = Math.max(0.1, Math.min(Math.PI - 0.1, spherical.phi));

        cameraPosition.setFromSpherical(spherical).add(baseCube.position);
        camera.position.copy(cameraPosition);
        camera.lookAt(baseCube.position);
    }

    previousMousePosition.x = event.clientX;
    previousMousePosition.y = event.clientY;
}

function onWheel(event) {
    event.preventDefault();

    const zoomAmount = event.deltaY * zoomSpeed;
    const cameraPosition = new THREE.Vector3();
    cameraPosition.copy(camera.position).sub(baseCube.position);

    if (
        (zoomAmount > 0 && cameraPosition.length() < maxDistance) ||
        (zoomAmount < 0 && cameraPosition.length() > minDistance)
    ) {
        cameraPosition.multiplyScalar(1 + zoomAmount / 100);
    }

    camera.position.copy(cameraPosition.add(baseCube.position));
    camera.lookAt(baseCube.position);
}

function onWindowResize() {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth / 2, window.innerHeight / 2);
}

function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
}
