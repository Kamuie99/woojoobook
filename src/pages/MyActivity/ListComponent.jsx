import React from 'react'
import styles from './ListComponent.module.css'

const ListComponent = ({ items, renderItem, emptyMessage }) => {
  return (
    <div className={styles.listContainer}>
      {items.length > 0 ? (
        items.map((item) => (
          <div key={item.id} className={styles.listItem}>
            {renderItem(item)}
          </div>
        ))
      ) : (
        <div className={styles.emptyList}>{emptyMessage}</div>
      )}
    </div>
  )
}

export default ListComponent