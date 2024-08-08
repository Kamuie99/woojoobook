import React from 'react'
import styles from './ListComponent.module.css'

const ListComponent = ({ items, emptyMessage, renderItem }) => {
  return (
    <>
      <div className={styles.listContainer}>
        {items.length > 0 ? (
          items.map((item, index) => (
            <div
              key={index}
              className={styles.listItem}
            >
              <div className={styles.listItemContent}>
                {renderItem(item)}
              </div>
            </div>
          ))
        ) : (
          <div className={styles.emptyList}>{emptyMessage}</div>
        )}
      </div>
    </>
  )
}

export default ListComponent;