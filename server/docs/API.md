### POST `{noty.server.apiContextPath}/publish/:publishId`

notification 을 발행합니다.

요청하는 서비스는, 요청 실패 시 동일한 publish id 로 재발행을 요청할 수 있습니다. 따라서 publish id 가 동일한 entity 가 이미 존재하는 경우에는 새로운 notification 을 생성하지 않아야 합니다.

**HEADER**

- `x-tenant-id` : tenant id

**Request / Response Body**

```typescript
interface RequestBody {
    metadata: Record<string, any> // server side metadata
    data: Record<string, any>
    consumableData: Record<string, any>
    secureData: Record<string, any>
    publicKey: JWK_JSON_OBJECT // secure data 을 암호화하기 위한 public Key
}

type ResponseCode = 201 | 200 | 400

interface ResponseBody {
    message: String | undefined
}
```

### PUT `{noty.server.apiContextPath}/publish/:publishId`

notification 을 수정합니다.

```typescript
interface RequestBody {
    data: Record<string, any>
    consumableData: Record<string, any>
    secureData: Record<string, any>
    publicKey: JWK_JSON_OBJECT // secure data 을 암호화하기 위한 public Key
}

type ResponseCode = 201 | 200 | 400
interface ResponseBody {
    message: String | undefined;
}
```

