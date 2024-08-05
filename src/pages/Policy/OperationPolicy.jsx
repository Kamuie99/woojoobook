import React from 'react'
import styles from './Policy.module.css'

const OperationPolicy = () => {
  // 운영정책
  return (
    <div>
      <div className={styles.section}>
        <h3>1. 서비스 이용 원칙</h3>
        <p>- 회원은 타인의 권리를 존중하고 실제로 보유한 도서만을 등록해야 합니다.</p>
        <p>- 도서의 상태를 정확히 기재하여 다른 회원들에게 정확한 정보를 제공해야 합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>2. 금지행위</h3>
        <p>다음과 같은 행위는 엄격히 금지됩니다:</p>
        <p>&nbsp;&nbsp;- 타인의 개인정보를 도용하거나 허위정보를 등록하는 행위</p>
        <p>&nbsp;&nbsp;- 저작권법을 위반하는 행위</p>
        <p>&nbsp;&nbsp;- 타인을 비방하거나 명예를 훼손하는 행위</p>
        <p>&nbsp;&nbsp;- 불법적이거나 부적절한 콘텐츠를 게시하는 행위</p>
        <p>&nbsp;&nbsp;- 서비스의 정상적인 운영을 방해하는 행위</p>
      </div>

      <div className={styles.section}>
        <h3>3. 도서 대여 및 교환 규칙</h3>
        <p>- 약속한 기간 내에 도서를 반납해야 합니다.</p>
        <p>- 도서의 훼손이나 분실 시 책임자는 즉시 상대방에게 알리고 적절한 보상을 해야 합니다.</p>
        <p>- 대여 및 교환 과정에서 발생하는 분쟁은 당사자 간 원만히 해결해야 합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>4. 커뮤니티 가이드라인</h3>
        <p>- 도서와 무관한 콘텐츠 게시를 금지합니다.</p>
        <p>- 스팸 및 광고성 게시물을 금지합니다.</p>
        <p>- 도서의 저작권을 침해하는 행위를 금지합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>5. 제재 조치</h3>
        <p>회사는 본 운영정책을 위반하는 회원에 대해 다음과 같은 제재를 가할 수 있습니다:</p>
        <p>&nbsp;&nbsp;- 경고</p>
        <p>&nbsp;&nbsp;- 게시물 삭제</p>
        <p>&nbsp;&nbsp;- 서비스 이용 제한</p>
        <p>&nbsp;&nbsp;- 회원 자격 정지 또는 영구 박탈</p>
      </div>

      <div className={styles.section}>
        <h3>6. 보증금 정책</h3>
        <p>도서 파손이나 분실에 대비하여 보증금을 받을 수 있으며, 구체적인 금액과 정책은 추후 공지할 예정입니다.</p>
      </div>

      <div className={styles.section}>
        <h3>7. 정책 변경</h3>
        <p>본 운영정책은 서비스의 변화에 따라 수정될 수 있으며, 중요한 변경사항이 있을 경우 공지사항을 통해 안내드리겠습니다.</p>
      </div>
    </div>
  )
}

export default OperationPolicy
