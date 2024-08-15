import { createContext, useState, useContext, useMemo } from 'react';

const SearchContext = createContext();

export const SearchProvider = ({ children }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const value = useMemo(() => ({
    searchTerm, setSearchTerm}),
    [searchTerm]
  );

  return (
    <SearchContext.Provider value={value}>
      {children}
    </SearchContext.Provider>
  );
};

export const useSearch = () => useContext(SearchContext);