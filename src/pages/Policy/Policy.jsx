import { useState } from 'react'
import { RiCustomerService2Line } from "react-icons/ri";
import Header from "../../components/Header"
import TermsOfService from "./TermsOfService"
import PrivacyPolicy from "./PrivacyPolicy"
import OperationPolicy from "./OperationPolicy"
import styles from './Policy.module.css'

const Policy = () => {
  const [activeTab, setActiveTab] = useState('terms');

  const renderContent = () => {
    switch (activeTab) {
      case 'terms':
        return <TermsOfService />;
      case 'privacy':
        return <PrivacyPolicy />;
      case 'operation':
        return <OperationPolicy />;
      default:
        return <TermsOfService />;
    }
  };
  return (
    <>
      <Header />
      <main>
        <div className={styles.titleDiv}>
          <RiCustomerService2Line />이용 약관 / 정책
        </div>
        <div className={styles.titleSub}>
          <p>우주도서를 이용해 주셔서 감사합니다.</p>
          <p>아래 준비한 약관을 읽어주시면 감사드리겠습니다.</p>
        </div>
        <div className={styles.policyPage}>
          <div className={styles.tabButtons}>
            <button
              className={`${styles.tabButton} ${activeTab === 'terms' ? styles.active : ''}`}
              onClick={() => setActiveTab('terms')}
            >
              이용약관
            </button>
            <button
              className={`${styles.tabButton} ${activeTab === 'privacy' ? styles.active : ''}`}
              onClick={() => setActiveTab('privacy')}
            >
              개인정보처리방침
            </button>
            <button
              className={`${styles.tabButton} ${activeTab === 'operation' ? styles.active : ''}`}
              onClick={() => setActiveTab('operation')}
            >
              운영정책
            </button>
          </div>
          <div className={styles.PolicyInner}>
            {renderContent()}
          </div>
        </div>
      </main>
  </>
  )
}

export default Policy