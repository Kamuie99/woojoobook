import React, { useEffect } from "react";
import { IoWifi } from "react-icons/io5";
import { MdBatteryCharging90 } from "react-icons/md";
import styles from './PhoneTopBar.module.css';

const PhoneTopBar = () => {
  const [currentTime, setCurrentTime] = React.useState(new Date());
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(interval);
  }, [])

  return (
    <div className={styles.phone_top_bar}>
      <div className={styles.current_time}>
        {currentTime.toLocaleTimeString([], {
          hour: 'numeric',
          minute: '2-digit',
          hour12: true,
          hourCycle: 'h12',
        })}
      </div>
      <div className={styles.top_bar_right}>
        <IoWifi />
        <p>75%</p>
        <MdBatteryCharging90 />
      </div>
      <div className={styles.camera}></div>
    </div>
  )
}

export default PhoneTopBar;