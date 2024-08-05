import React from 'react'
import styles from './Policy.module.css'

const TermsOfService = () => {
  // 이용약관
  return (
    <div>
      <div className={styles.section}>
        <h3>제1조 (목적)</h3>
        <p>본 약관은 우주도서(이하 "회사")가 제공하는 P2P 도서 교환 및 대여 서비스(이하 "서비스")의 이용 조건 및 절차, 회사와 회원 간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>
      </div>
      <div className={styles.section}>
        <h3>제2조 (정의)</h3>
        <p>1. "회원"이란 본 약관에 동의하고 회사와 서비스 이용계약을 체결한 자를 말합니다.</p>
        <p>2. "책권자"란 도서를 대여해주는 회원을 말합니다.</p>
        <p>3. "책무자"란 도서를 대여하는 회원을 말합니다.</p>
        <p>4. "교환신청자"란 도서 교환을 신청하는 회원을 말합니다.</p>
        <p>5. "교환승인자"란 도서 교환 신청을 받는 회원을 말합니다.</p>
        <p>6. "발신자"란 채팅을 보내는 회원을 말합니다.</p>
        <p>7. "수신자"란 채팅을 받는 회원을 말합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>제3조 (서비스 이용)</h3>
        <p>1. 회원은 포인트를 사용하여 도서를 대여할 수 있습니다.</p>
        <p>2. 도서 대여 기간은 기본 7일이며, 연장 시 7일이 추가됩니다.</p>
        <p>3. 회원의 포인트가 0 이하가 될 경우 서비스 이용이 영구 정지됩니다.</p>
        <p>4. 연체 3일 경과 시 서비스 이용이 영구 정지됩니다.</p>
      </div>

      <div className={styles.section}>
        <h3>제4조 (포인트)</h3>
        <p>회원은 서비스를 이용하여 포인트를 획득하고 사용할 수 있습니다.</p>
        <p>1. 포인트 변화</p>
        <p>&nbsp;&nbsp;- 도서 등록: +200점</p>
        <p>&nbsp;&nbsp;- 도서 대여(책권자): +200점</p>
        <p>&nbsp;&nbsp;- 도서 교환: +200점</p>
        <p>&nbsp;&nbsp;- 출석: +10점</p>
        <p>&nbsp;&nbsp;- 도서 대여(책무자): -100점</p>
        <p>&nbsp;&nbsp;- 연체: 일수에 따라 -100/ -300/ -500점, 이후 영구정지</p>
      </div>

      <div className={styles.section}>
        <h3>제5조 (회원의 의무)</h3>
        <p>1. 회원은 본 약관 및 회사가 서비스 내에 게시하는 운영정책을 준수해야 합니다.</p>
        <p>2. 회원은 다른 회원의 도서를 소중히 다루어야 하며, 파손 또는 분실 시 책임을 져야 합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>제6조 (서비스 변경 및 중지)</h3>
        <p>1. 회사는 시스템 보수, 교체 및 고장, 통신 두절 등의 사유가 발생한 경우에는 서비스의 제공을 일시적으로 중단할 수 있습니다.</p>
        <p>2. 회사는 천재지변, 전쟁, 국가비상사태 등의 불가항력으로 인해 서비스 제공이 불가능할 경우, 서비스의 전부 또는 일부를 제한하거나 중단할 수 있습니다.</p>
      </div>

      <div className={styles.section}>
        <h3>제7조(회원 탈퇴 및 자격 상실)</h3>
        <p>1. 회원은 언제든지 탈퇴를 요청할 수 있으며, 회사는 이를 즉시 처리합니다.</p>
        <p>2. 회사는 회원이 다음 각 호의 사유에 해당하는 경우, 회원 자격을 제한하거나 정지시킬 수 있습니다:</p>
        <p>- 가입 신청 시 허위 내용을 등록한 경우</p>
        <p>- 다른 사람의 서비스 이용을 방해하거나 그 정보를 도용하는 등 질서를 위협하는 경우</p>
        <p>- 법령 및 본 약관이 금지하거나 공서양속에 반하는 행위를 하는 경우</p>
      </div>

      <div className={styles.section}>
        <h3>제8조 (약관의 변경)</h3>
        <p>1. 회사는 필요한 경우 약관을 변경할 수 있으며, 변경된 약관은 서비스 내에 공지함으로써 효력이 발생합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>제9조 (준거법 및 관할법원)</h3>
        <p>1. 본 약관의 해석 및 분쟁에 대한 소송은 대한민국 법을 준거법으로 하며, 회사의 본점 소재지를 관할하는 법원을 제1심 전속적 관할법원으로 합니다.</p>
      </div>
    </div>
  )
}

export default TermsOfService
