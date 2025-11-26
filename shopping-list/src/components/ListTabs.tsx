import React from 'react';
import { IonSegment, IonSegmentButton, IonLabel, IonButton, IonIcon } from '@ionic/react';
import { add, trashOutline } from 'ionicons/icons';
import { ShoppingList } from '../types';

interface ListTabsProps {
  lists: ShoppingList[];
  activeListId: string | null;
  onSelectList: (id: string) => void;
  onAddList: () => void;
  onDeleteList: (id: string) => void;
}

const ListTabs: React.FC<ListTabsProps> = ({ 
  lists, 
  activeListId, 
  onSelectList, 
  onAddList,
  onDeleteList 
}) => {
  return (
    <div style={{ display: 'flex', alignItems: 'center', padding: '0 10px', background: 'var(--ion-background-color)' }}>
      <div style={{ flex: 1, overflowX: 'auto' }}>
        {lists.length > 0 && (
          <IonSegment 
            scrollable 
            value={activeListId || lists[0].id} 
            onIonChange={e => onSelectList(e.detail.value as string)}
            mode="md" // Material design looks cleaner for segments usually
          >
            {lists.map(list => (
              <IonSegmentButton key={list.id} value={list.id}>
                <IonLabel>{list.name}</IonLabel>
              </IonSegmentButton>
            ))}
          </IonSegment>
        )}
      </div>
      
      <IonButton fill="clear" onClick={onAddList} size="small">
        <IonIcon icon={add} slot="icon-only" />
      </IonButton>
      
      {activeListId && (
        <IonButton fill="clear" color="danger" onClick={() => onDeleteList(activeListId)} size="small">
          <IonIcon icon={trashOutline} slot="icon-only" />
        </IonButton>
      )}
    </div>
  );
};

export default ListTabs;