import { useContext, useEffect, useState, useMemo } from "react"
import { useParams } from 'react-router-dom';
import { AuthContext } from "../../contexts/AuthContext"
import Header from "../../components/Header"
import axiosInstance from "../../util/axiosConfig"
import Modal from "../BookRegister/Modal";
import CategoryForm from "./CategoryForm";
import CategoryItem from "./CategoryItem";
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import styles from './MyLibrary.module.css';

const MyLibrary = () => {
  const {userId} = useParams()
  const {sub: loggedInUserId} = useContext(AuthContext)
  const [isOwnLibrary, setIsOwnLibrary] = useState(false)
  const [categories, setCategories] = useState([])
  const [createModalIsOpen, setCreateModalIsOpen] = useState(false);
  const [updateModalIsOpen, setUpdateModalIsOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);

  useEffect(() => {
    setIsOwnLibrary(userId === loggedInUserId)
    fetchCategories()
  }, [userId, loggedInUserId])

  const fetchCategories = async () => {
    try {
      const response = await axiosInstance.get(`/users/${userId}/libraries`)
      setCategories(response.data.libraryList)
    } catch (error) {
      console.log(error)
    }
  }

  const sortedCategories = useMemo(() => {
    return [...categories].sort((a, b) => a.orderNumber - b.orderNumber)
  }, [categories])

  const handleCreate = async () => {
    setCreateModalIsOpen(true)
  }

  const handleUpdate = (category) => {
    setSelectedCategory(category)
    setUpdateModalIsOpen(true)
  }

  const handleDelete = async (categoryId) => {
    if (window.confirm("정말로 이 카테고리를 삭제하시겠습니까?")) {
      try {
        await axiosInstance.delete(`/users/${userId}/libraries/categories/${categoryId}`)
        setCategories(categories.filter(cat => cat.id !== categoryId))
      } catch (error) {
        console.log(error)
        alert("카테고리 삭제에 실패했습니다.")
      }
    }
  }

  const handleCreateSubmit = async (newCategory) => {
    try {
      const response = await axiosInstance.post(`/users/${userId}/libraries/categories`, newCategory)
      setCategories([...categories, response.data])
      setCreateModalIsOpen(false);
    } catch (error) {
      console.log(error)
    }                                                                                                                                   
  }

  const handleUpdateSubmit = async (updatedCategory) => {
    try {
      const response = await axiosInstance.put(`/users/${userId}/libraries/categories/${selectedCategory.id}`, updatedCategory)
      setCategories(categories.map(cat => cat.id === selectedCategory.id ? response.data : cat))
      setUpdateModalIsOpen(false)
      setSelectedCategory(null)
    } catch (error) {
      console.log(error)
    }
  }

  const moveCategory = (draggedCategory, targetCategory) => {
    const updatedCategories = [...categories];
    const dragIndex = updatedCategories.findIndex(cat => cat.id === draggedCategory.id);
    const targetIndex = updatedCategories.findIndex(cat => cat.id === targetCategory.id);

    [updatedCategories[dragIndex], updatedCategories[targetIndex]] = [updatedCategories[targetIndex], updatedCategories[dragIndex]];

    setCategories(updatedCategories);
    saveCategoryOrder(draggedCategory, targetCategory);
  };

  const saveCategoryOrder = async (fromCategory, toCategory) => {
    try {
      await axiosInstance.put(`/users/${userId}/libraries/categories/${fromCategory.id}/${toCategory.id}`);
      fetchCategories();
    } catch (error) {
      console.error("카테고리 순서 교환에 실패했습니다:", error);
    }
  };

  const handleEmptyBoxClick = (category) => {
    setSelectedCategory(category);
    setUpdateModalIsOpen(true);
  };
  
  return (
    <DndProvider backend={HTML5Backend}>
      <Header />
        {isOwnLibrary ? (
          <main className={styles.main}>
            <p className={styles.libraryTitle}>나의 서재</p>
            <div className={styles.categoriesContainer}>
              {sortedCategories.map((category, index) => (
                <CategoryItem
                  key={category.id}
                  index={index}
                  category={category}
                  isOwnLibrary={isOwnLibrary}
                  onUpdate={isOwnLibrary ? () => handleUpdate(category) : null}
                  onDelete={isOwnLibrary ? () => handleDelete(category.id) : null}
                  moveCategory={isOwnLibrary ? moveCategory : null}
                  saveCategoryOrder={isOwnLibrary ? saveCategoryOrder : null}
                  onEmptyBoxClick={() => handleEmptyBoxClick(category)}
                />
              ))}
            </div>
            <button onClick={handleCreate} className={styles.createButton}>카테고리 생성</button>

            <Modal
              isOpen={createModalIsOpen}
              onRequestClose={() => setCreateModalIsOpen(false)}
              contentLabel="카테고리 생성"
            >
              <CategoryForm
                onSubmit={handleCreateSubmit}
                action="생성하기"
              />
            </Modal>

            <Modal
              isOpen={updateModalIsOpen}
              onRequestClose={() => setUpdateModalIsOpen(false)}
              contentLabel="카테고리 수정"
            >
              <CategoryForm
                initialCategory={selectedCategory}
                onSubmit={handleUpdateSubmit}
                action="수정하기"
              />
            </Modal>
          </main>
        ) : (
          <main className={styles.main}>
            <p className={styles.libraryTitle}>남의 서재</p>
            <div className={styles.categoriesContainer}>
              {sortedCategories.map(category => (
                <CategoryItem
                  key={category.id}
                  category={category}
                />
              ))}
            </div>
          </main>
        )}
    </DndProvider>
    
  )
}

export default MyLibrary
