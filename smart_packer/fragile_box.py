# 박스 정보 (가로, 세로, 높이 단위: mm)
boxes = [
    {"id": 1, "dimensions": (220, 190, 90)},
    {"id": 2, "dimensions": (270, 180, 150)},
    {"id": 3, "dimensions": (350, 250, 100)},
    {"id": 4, "dimensions": (340, 250, 120)},
    {"id": 5, "dimensions": (410, 310, 280)},
    {"id": 6, "dimensions": (480, 380, 340)}
]

def check_exceptions(item_dimensions):
    # 예외 조건 1: 가로, 세로, 높이 합이 50cm 이하인 물건
    if sum(item_dimensions) <= 500:
        return True

    # 예외 조건 2: 짧은 두 변 길이가 각각 가장 긴 길이의 20% 이하인 것
    sorted_dimensions = sorted(item_dimensions)
    if sorted_dimensions[0] <= 0.2 * sorted_dimensions[2] and sorted_dimensions[1] <= 0.2 * sorted_dimensions[2]:
        return True

    # 예외 조건 3: 두 번째로 긴 변의 길이가 가장 짧은 길이의 4배 이상인 것
    if sorted_dimensions[1] >= 4 * sorted_dimensions[0]:
        return True

    return False

def check_gap_between_item_and_box(item_dimensions, box_dimensions):
    # 박스와 물건 간의 간격이 5cm 이상인지 체크
    for i in range(3):
        if box_dimensions[i] - item_dimensions[i] < 50:
            return False
    return True

def find_box(item_dimensions, item_weight, fragile):
    # 물건의 가로, 세로, 높이를 정렬해 모든 경우의 배치를 고려
    permutations = [
        (item_dimensions[0], item_dimensions[1], item_dimensions[2]),
        (item_dimensions[0], item_dimensions[2], item_dimensions[1]),
        (item_dimensions[1], item_dimensions[0], item_dimensions[2]),
        (item_dimensions[1], item_dimensions[2], item_dimensions[0]),
        (item_dimensions[2], item_dimensions[0], item_dimensions[1]),
        (item_dimensions[2], item_dimensions[1], item_dimensions[0])
    ]

    # 포장 가능한 크기의 박스 찾기
    suitable_boxes = []
    for box in boxes:
        for perm in permutations:
            if check_gap_between_item_and_box(perm, box["dimensions"]):
                suitable_boxes.append(box)
                break

    # 2. 최대 크기 박스보다 물건이 크면 종료
    if not suitable_boxes:
        return "포장할 수 있는 적합한 박스가 없습니다."

    # 3. 적합한 박스 중에서 무게 확인
    if item_weight > 30000:  # 30kg 이상
        return "포장할 수 있는 적합한 박스가 없습니다."

    # 예외 조건 검사
    is_exception = check_exceptions(item_dimensions)

    # 4. 적합한 박스 중에서 선택
    for box in suitable_boxes:
        box_volume = box["dimensions"][0] * box["dimensions"][1] * box["dimensions"][2]
        for perm in permutations:
            if check_gap_between_item_and_box(perm, box["dimensions"]):
                item_volume = perm[0] * perm[1] * perm[2]
                
                # 예외 조건을 만족하면 박스 공간 비율을 신경쓰지 않음
                if not is_exception:
                    if item_volume < 0.3 * box_volume:
                        continue  # 박스 공간의 최소 30%를 차지하지 않으면 넘어감

                if fragile:
                    # 취급주의: 박스의 70%를 초과하면 한 단계 큰 박스 선택
                    if item_volume > 0.7 * box_volume:
                        continue
                    empty_space_percentage = ((box_volume - item_volume) / box_volume) * 100
                    return (
                        f"가장 적합한 박스: {box['id']}호, "
                        f"남은 공간: {empty_space_percentage:.2f}%, "
                        "완충재를 채워주세요."
                    )
                else:
                    empty_space_percentage = ((box_volume - item_volume) / box_volume) * 100
                    return (
                        f"가장 적합한 박스: {box['id']}호, "
                        f"남은 공간: {empty_space_percentage:.2f}%"
                    )
    return "포장할 수 있는 적합한 박스가 없습니다."

# 사용자 입력 받기
try:
    print("물건의 가로, 세로, 높이, 무게를 입력하세요 (단위: mm, g, 공백으로 구분):")
    user_input = list(map(int, input().split()))
    if len(user_input) != 4:
        print("잘못된 입력입니다. 가로, 세로, 높이, 무게를 정확히 입력하세요.")
    else:
        item_dimensions = tuple(user_input[:3])
        item_weight = user_input[3]

        # 최대 크기 박스보다 물건이 큰지 확인
        max_box_dimensions = max(boxes, key=lambda b: b["dimensions"])
        if any(item_dimensions[i] > max_box_dimensions["dimensions"][i] for i in range(3)):
            print("포장할 수 있는 적합한 박스가 없습니다.")
        elif item_weight > 30000:
            print("30kg 초과 입니다.")
        else:
            print("취급주의 여부를 입력하세요 (0: 아니오, 1: 예):")
            fragile = int(input())
            result = find_box(item_dimensions, item_weight, fragile)
            print(result)

except ValueError:
    print("잘못된 입력입니다. 숫자를 정확히 입력하세요.")
