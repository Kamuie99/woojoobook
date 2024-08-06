import styles from './Policy.module.css'

const PrivacyPolicy = () => {
  // 개인정보처리방침
  return (
    <>
      <div className={styles.section}>
        <h3>1. 수집하는 개인정보의 항목 및 수집방법</h3>
        <p>회사는 회원가입 시 다음과 같은 개인정보를 수집합니다.</p>
        <p>&nbsp;&nbsp;- 이메일 주소</p>
        <p>&nbsp;&nbsp;- 비밀번호</p>
        <p>&nbsp;&nbsp;- 주소</p>
      </div>

      <div className={styles.section}>
        <h3>2. 개인정보의 수집 및 이용목적</h3>
        <p>회사는 수집한 개인정보를 다음의 목적을 위해 이용합니다.</p>
        <p>&nbsp;&nbsp;- 회원 식별 및 가입의사 확인</p>
        <p>&nbsp;&nbsp;- 서비스 제공 및 관리</p>
        <p>&nbsp;&nbsp;- 도서 대여 및 교환 서비스 제공</p>
        <p>&nbsp;&nbsp;- 공지사항 전달</p>
      </div>

      <div className={styles.section}>
        <h3>3. 개인정보의 보유 및 이용기간</h3>
        <p>- 회원의 개인정보는 회원 탈퇴 시 지체 없이 파기됩니다.</p>
        <p>- 단, 관련 법령에 따라 일정 기간 보관이 필요한 경우 해당 기간 동안 보관됩니다.</p>
      </div>

      <div className={styles.section}>
        <h3>4. 개인정보의 파기절차 및 방법</h3>
        <p>- 회사는 개인정보 보유기간의 경과, 처리목적 달성 등 개인정보가 불필요하게 되었을 때에는 지체 없이 해당 개인정보를 파기합니다.</p>
        <p>- 정보는 복구 및 재생이 불가능한 방법을 사용하여 삭제합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>5. 개인정보의 제3자 제공</h3>
        <p>- 회사는 회원의 개인정보를 원칙적으로 외부에 제공하지 않습니다.</p>
        <p>- 단, 회원의 동의가 있거나 법령의 규정에 의한 경우는 예외로 합니다.</p>
      </div>

      <div className={styles.section}>
        <h3>6. 이용자 및 법정대리인의 권리와 그 행사방법</h3>
        <p>회원은 언제든지 등록되어 있는 자신의 개인정보를 조회하거나 수정할 수 있으며, 회원탈퇴를 통해 개인정보의 삭제를 요청할 수 있습니다.</p>
      </div>

      <div className={styles.section}>
        <h3>7. 개인정보 보호책임자</h3>
        <p>회사는 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.</p>
        <br />
        <p>&nbsp;&nbsp;개인정보 보호책임자</p>
        <p>&nbsp;&nbsp;- 이름: E207</p>
        <p>&nbsp;&nbsp;- 연락처: e207.woojoobook@gmail.com</p>
      </div>

      <div className={styles.section}>
        <h3>8. 개인정보 처리방침의 변경</h3>
        <p>본 개인정보처리방침은 법령, 정책 또는 보안기술의 변경에 따라 내용의 추가, 삭제 및 수정이 있을 시 홈페이지를 통해 공지하도록 하겠습니다.</p>
      </div>
    </>
  )
}

export default PrivacyPolicy
