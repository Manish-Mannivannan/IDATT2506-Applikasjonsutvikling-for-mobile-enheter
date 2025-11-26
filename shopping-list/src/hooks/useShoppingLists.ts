import { useState, useEffect, useRef } from 'react';
import { ShoppingList, ListItem } from '../types';
import { loadLists, saveLists } from '../utils/storage';

export const useShoppingLists = () => {
  const [lists, setLists] = useState<ShoppingList[]>([]);
  const [activeListId, setActiveListId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  
  // Ref to track if initial load is done to prevent overwriting with empty state
  const loadedRef = useRef(false);

  useEffect(() => {
    const init = async () => {
      const loadedLists = await loadLists();
      setLists(loadedLists);
      if (loadedLists.length > 0) {
        setActiveListId(loadedLists[0].id);
      }
      loadedRef.current = true;
      setLoading(false);
    };
    init();
  }, []);

  useEffect(() => {
    if (loadedRef.current) {
      saveLists(lists);
    }
  }, [lists]);

  const createList = (name: string) => {
    const newList: ShoppingList = {
      id: Date.now().toString(),
      name,
      items: []
    };
    setLists(prev => [...prev, newList]);
    setActiveListId(newList.id);
  };

  const deleteList = (id: string) => {
    setLists(prev => {
      const newLists = prev.filter(l => l.id !== id);
      // If we deleted the active list, switch to another one or null
      if (activeListId === id) {
        setActiveListId(newLists.length > 0 ? newLists[0].id : null);
      }
      return newLists;
    });
  };

  const addItem = (listId: string, text: string) => {
    const newItem: ListItem = {
      id: Date.now().toString(),
      text,
      done: false
    };
    setLists(prev => prev.map(list => {
      if (list.id === listId) {
        // Add new items to the top
        return { ...list, items: [newItem, ...list.items] };
      }
      return list;
    }));
  };

  const toggleItemDone = (listId: string, itemId: string) => {
    setLists(prev => prev.map(list => {
      if (list.id === listId) {
        return {
          ...list,
          items: list.items.map(item =>
            item.id === itemId ? { ...item, done: !item.done } : item
          )
        };
      }
      return list;
    }));
  };

  // Replaces the items array for a specific list (used for reordering)
  const updateListItems = (listId: string, newItems: ListItem[]) => {
    setLists(prev => prev.map(list => {
      if (list.id === listId) {
        return { ...list, items: newItems };
      }
      return list;
    }));
  };

  return {
    lists,
    activeListId,
    loading,
    setActiveListId,
    createList,
    deleteList,
    addItem,
    toggleItemDone,
    updateListItems
  };
};