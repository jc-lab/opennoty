### POST `{noty.server.apiContextPath}/publish/:publishId`

notification 을 발행합니다.

요청하는 서비스는, 요청 실패 시 동일한 publish id 로 재발행을 요청할 수 있습니다. 따라서 publish id 가 동일한 entity 가 이미 존재하는 경우에는 새로운 notification 을 생성하지 않아야 합니다.

**HEADER**

- `x-tenant-id` : tenant id

**Request / Response Body**

```typescript
interface PublishRequestBody {
    /**
     * 서버 측에만 저장되는 메타데이터
     */
    metadata: Record<string, any> | null
    /**
     * 프론트로 전달되는 Notification Data
     */
    data: Record<string, any> | null
    /**
     * 프론트로 전달되는 Notification Data
     * DB 에 저장되지 않고 update 당시 활성화 된 Browser 에게만 전달한다.
     * 빠르게 업데이트 되는 실시간 progress 등을 전달하기 위해 사용한다.
     */
    consumableData: Record<string, any>
    /**
     * 암호화해서 전달해야 하는 경우에 publicKey 와 함께 전달한다.
     */
    secureData: Record<string, any>
    /**
     * secureData 을 사용하는 경우에 JWK ECDH-ES 공개 키를 전달한다.
     */
    publicKey: JWK_JSON_OBJECT // secure data 을 암호화하기 위한 public Key

    recipients: Array<Recipient> // 수신자 목록
}

interface Recipient {
    /**
     * 전달 방식
     * - notification : 브라우저(in-app) 노티
     * - email : 이메일 전송
     */
    method: 'notification' | 'email'
    
    userId: string // 수신자 User ID

    email: string // method 가 email 인 경우, 수신자 이메일
    locale: string // method 가 email 인 경우, 수신자 Locale (e.g. en-us / ko-kr)
}

// 200 : 성공
// 400 : 잘못된 데이터 구조
type ResponseCode = 200 | 400

interface BasicResponseBody {
    message: String | undefined
}
```

### PUT `{noty.server.apiContextPath}/publish/:publishId`

notification 을 수정합니다.

```typescript
interface PublishUpdateRequestBody {
    /**
     * 서버 측에만 저장되는 메타데이터
     */
    metadata: Record<string, any> | null
    /**
     * 프론트로 전달되는 Notification Data
     */
    data: Record<string, any> | null
    /**
     * 프론트로 전달되는 Notification Data
     * DB 에 저장되지 않고 update 당시 활성화 된 Browser 에게만 전달한다.
     * 빠르게 업데이트 되는 실시간 progress 등을 전달하기 위해 사용한다.
     */
    consumableData: Record<string, any>
    /**
     * 암호화해서 전달해야 하는 경우에 publicKey 와 함께 전달한다.
     */
    secureData: Record<string, any>
}

// 200 : 성공
// 400 : 잘못된 데이터 구조
type ResponseCode = 200 | 400

interface BasicResponseBody {
    message: String | undefined
}
```
