# Assignment - My-Storage

- 해당 과제는 총 7개의 Step으로 구성되어 있습니다.
- 모든 과제는 C4-Cometrue 깃허브 레포에서 관리되어야 하며, 반드시 각 Step이 종료될 때 마다 PR 요청을 날려야 합니다.
    - 물론, 다른 사람들의 PR에 대한 리뷰도 가능합니다.
- 서버는 필요할 때 편하게 말씀해 주세요.
- 상당히 고민이 많이 필요한 주제입니다. 일부 Step에 대해서 제공하는 키워드가 힌트가 될 수 있으니, 키워드와 관련한 학습을 진행하는 것을 권장합니다.
- 궁금증이 있으면 주저하지 말고 서로 커뮤니케이션하고, 그럼에도 해결되지 않는다면 질문을 남겨주세요.
- 각 PR 마다 설계 의도를 작성해주세요. 좀 더 효율적인 리뷰가 가능할 겁니다.

## 프로젝트 설명

`간단한 형태의 클라우드 스토리지 개발`

- Google Drive, Dropbox, Naver Mybox 와 같이, 파일을 온라인에 저장하고 관리하는 것을 우리는 "클라우드 스토리지" 라고 부릅니다.
- 이번에는 기본적인 파일 과업들을 수행하는 클라우드 스토리지를 개발하는 것이 목표입니다.
- 단순 파일 업로드 자체는 서버 프레임워크들이 이미 지원하고 있습니다. 우리들은 파일 업로드에는 집중하지 않고, 실질적인 과업들에 대한 논리적인 구현에 집중합시다.

### Step 1. 파일 업로드, 삭제

#### 구현사항

- 우선, 폴더는 없다고 생각하고 파일 업로드를 진행해봅시다.✅
- 파일을 업로드 할 경우, 관련된 메타데이터를 DB에 저장해야 합니다. 필수적으로 저장해야 할 데이터를 고려하여 DB에 저장하도록 해 주세요.✅
- 로그인은 구현하지 않을 것 입니다. 따라서 업로드한 유저의 정보도 같이 전달해 주도록 하세요.✅
- 서로 다른 사용자가 같은 이름의 파일을 업로드 할 수 있습니다. 이 경우, 서버에 저장되는 파일 이름은 어떻게 해야할까요?✅
- 동일하게, 파일 삭제도 구현해 주세요.
- 당연하지만 파일이 존재하지 않으면 오류를 반환해야 합니다. 물리적으로도 삭제되도록 해 주세요.✅
- 파일 다운로드도 구현해야 합니다.✅
- 우선은 본인이 만든 파일이 아니면 다운이 불가능하게 설정해주세요.✅

#### 프로그래밍 요구사항

- 파일 시스템의 관점으로 접근해 봅시다. 실제 파일과 메타데이터는 어떻게 매칭되어야 할까요?✅
- 물리적 I/O 요청이 코드에 잔뜩 포함되어 있을 수 있습니다. 관점을 잘 분리해서 코드를 작성해 주세요.

### Step 2. 파일 및 폴더 (1)

#### 구현사항

- 이제, 폴더의 존재가 생깁니다. 폴더를 생성하고 이름을 변경하는 API를 개발해주세요.
- 당연하겠지만, 여기서도 사용자 구분이 필요합니다.
    - 또한, 이미 존재하는 폴더를 또 만들 순 없겠죠?
- 파일의 메타데이터 정보에 위치 폴더 존재도 추가되어야 합니다.
- 그리고 일반적인 클라우드 스토리지 처럼, 특정 폴더에 대한 요청 시 폴더에 포함된 파일/폴더의 메타데이터 목록을 반환하는 api 또한 개발해야 합니다.

#### 프로그래밍 요구사항

- 어떤 파일이 특정 폴더에 있는지 어떻게 알 수 있을까요? 폴더와 파일은 어떻게 구분할 수 있을까요?

### Step 3. 파일 및 폴더 (2)

#### 구현사항

- 폴더 삭제를 구현해 주세요.
- 폴더 삭제 시, 하위의 모든 파일, 폴더가 삭제되어야 합니다.
- 파일 이동, 폴더 이동도 구현해 주세요.
- 여기서도 마찬가지로 폴더 이동 시, 하위의 모든 파일이 전부 이동해야 합니다.
- (여유가 된다면) 복사도 구현해 볼까요?

#### 프로그래밍 요구사항

- 알고리즘을 활용해야 합니다. 파일 시스템의 전체 구조는 트리 형태를 띄고 있다는 점이 힌트가 되겠네요.

### Step 4. 폴더 요약, 전체 요약

#### 구현사항

- 다음과 같이, 특정 폴더의 정보를 확인할 수 있는 기능을 구현해 주세요.
- 만든 날짜는 최초 1회에 한 해 설정되며, 절대 변경되지 않습니다. 수정한 날짜는 다음과 같은 과업이 수행될 때 변경됩니다.
    - 파일, 폴더가 생성된 경우
    - 파일, 폴더의 이름이 변경되거나, 위치가 변경된 경우
    - (폴더의 경우) 해당 폴더 내에서 파일 과업 (업로드, 삭제, 이름 변경, 복사, 이동)이 발생한 경우
- 또한, 루트 폴더에서 정보 요약을 할 경우, 다른 결과가 나와야 합니다.
    - 전체 폴더의 수, 파일의 수, 가용 용량 및 사용중인 용량이 나와야 합니다.
- 일단은 가용 용량은 인당 최대 2GB로 제한합니다.
    - 즉, 전체 파일의 크기가 2GB를 넘어간다면, 더 이상 파일 업로드가 불가능 합니다.

#### 프로그래밍 요구사항

- 정보 요약은 상당히 부하가 많이 갈 수 있는 작업입니다. 어떻게 처리해야 좀 더 부하를 줄일 수 있을지 고민해 보세요.
- 특히, 하위 폴더 구조가 깊으면 깊을수록 부하가 더 강해질 수 있습니다. 직접 하위 폴더를 최대한으로 만들어서 계산해 보세요.

### Step 5. 파일 공유하기

#### 구현사항

- 파일을 공유하는 방법을 생각해봅시다.
    - 현재는 본인의 파일이 아니면 다운로드가 불가능합니다.
    - 그렇기에, 다른 사람들이 파일을 다운로드 할 수 있도록 초대 링크를 생성해주는 API를 개발하려고 합니다.
- 만들어진 링크의 유효기간은 3시간입니다.
    - 만약 링크의 정보를 DB에 저장했거나 파일 시스템에 저장했다면, 제거가 이루어지도록 해 주세요.
    - 물론, 3시간이 경과되지 않았음에도 링크를 제거할 수 있습니다.
- 폴더에 대한 링크를 생성할 경우, 다른 사람들도 파일 업로드/파일 삭제가 가능합니다.
    - 이 경우, 파일 소유권은 원 폴더 소유주로 넘어간다는 사실을 기억해 주세요.
    - 즉, 링크가 만료되면 파일을 업로드 한 사람도 해당 파일에 접근할 수 없게 됩니다.

#### 프로그래밍 요구사항

- 3시간 땡 했다고 바로 지울 필요는 없습니다. 좀 더 부하가 가지 않는 방법을 고민해 보세요.

### Step 6. 고급 파일 작업, 파일 조회

#### 구현사항

- 일반적으로 우리가 스토리지 서비스를 이용하면, 이미지 같은 경우는 썸네일을 제공하고 있습니다.
- 특정 폴더의 파일을 조회하는 기능이 없다면 새로 만들어주시고, 있다면 다음과 같은 기능을 만족할 수 있도록 수정해 주세요.
    - 이미지 파일의 경우, Preview에서 썸네일을 제공해야 합니다.
    - 잘 알려진 파일 타입의 경우, 파일의 타입도 같이 넘겨줄 수 있도록 해야 합니다. (ex. IMAGE, DOCUMENT, ZIP)
    - 폴더 -> 파일 순으로 데이터를 제공해야 하며, 한 페이지에 최대 100개의 파일/폴더만 표시해야 합니다.

#### 프로그래밍 요구사항

- 썸네일 추출에 어떤 방법을 사용하던, 썸네일 추출은 자원을 많이 소모할 수 밖에 없습니다. 어떻게 구축하는게 좋을까요?
- 어떤 파일을 이미지 파일이라고 할 수 있을까요? 좀 더 확장해서, 어떤 파일을 압축 파일이라고 할 수 있고, 어떤 파일을 문서 파일이라고 할 수 있을까요?
- 파일 덮어쓰기를 할 경우, 썸네일 이미지가 변경될 수도 있고, 그렇지 않을 수도 있습니다. 어떤 기준으로 처리해야 할까요?

### Step 7. 성능 테스트

#### 구현사항

- 실제로 개발한 서버의 성능이 어떨까요?
- 사용자의 사용 시나리오를 설계하고, 이를 활용해 스트레스 테스트 툴을 사용한 성능 테스트를 진행해봅시다.

#### 키워드

- Stress Test (ngrinder)
- TPS
