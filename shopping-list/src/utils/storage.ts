import { Filesystem, Directory, Encoding } from '@capacitor/filesystem';
import { ShoppingList } from '../types';

const FILENAME = 'shopping-lists.json';

export const saveLists = async (lists: ShoppingList[]): Promise<void> => {
  try {
    await Filesystem.writeFile({
      path: FILENAME,
      data: JSON.stringify(lists),
      directory: Directory.Data,
      encoding: Encoding.UTF8,
    });
  } catch (error) {
    console.error('Error saving lists:', error);
  }
};

export const loadLists = async (): Promise<ShoppingList[]> => {
  try {
    const result = await Filesystem.readFile({
      path: FILENAME,
      directory: Directory.Data,
      encoding: Encoding.UTF8,
    });
    // Capacitor returns data as string for UTF8 encoding
    return JSON.parse(result.data as string);
  } catch (error) {
    // File likely doesn't exist on first run
    console.log('No existing lists found, starting fresh.');
    return [];
  }
};