import React from 'react';
import { 
  IonList, 
  IonItem, 
  IonLabel, 
  IonCheckbox, 
  IonReorderGroup, 
  IonReorder, 
  IonIcon,
  IonButton,
  ItemReorderEventDetail
} from '@ionic/react';
import { menuOutline, trashOutline } from 'ionicons/icons';
import { ListItem } from '../types';

interface ListItemsProps {
  items: ListItem[];
  onToggleItemDone: (id: string) => void;
  onDeleteItem: (id: string) => void;
  onUpdateItems: (items: ListItem[]) => void;
}

const ListItems: React.FC<ListItemsProps> = ({ items, onToggleItemDone, onDeleteItem, onUpdateItems }) => {
  const notDoneItems = items.filter(i => !i.done);
  const doneItems = items.filter(i => i.done);

  const handleReorder = (event: CustomEvent<ItemReorderEventDetail>) => {
    // The reorder event gives us indexes relative to the rendered list (notDoneItems)
    const fromIndex = event.detail.from;
    const toIndex = event.detail.to;

    // Move item within the notDone array
    const draggedItem = notDoneItems[fromIndex];
    const newNotDone = [...notDoneItems];
    newNotDone.splice(fromIndex, 1);
    newNotDone.splice(toIndex, 0, draggedItem);

    // Combine back with done items to update the full list
    const newFullList = [...newNotDone, ...doneItems];
    
    onUpdateItems(newFullList);
    event.detail.complete();
  };

  return (
    <>
      {/* Active Items (Reorderable) */}
      <IonList lines="full" style={{ background: 'transparent' }}>
        <IonReorderGroup disabled={false} onIonItemReorder={handleReorder}>
          {notDoneItems.map((item) => (
            <IonItem key={item.id}>
              <IonCheckbox 
                slot="start" 
                checked={item.done} 
                onIonChange={() => onToggleItemDone(item.id)}
                style={{ '--size': '22px' }}
              />
              <IonLabel style={{ fontWeight: '500' }}>{item.text}</IonLabel>
              
              {/* Actions: Delete Button + Reorder Handle */}
              <div slot="end" style={{ display: 'flex', alignItems: 'center' }}>
                <IonButton 
                  fill="clear" 
                  color="medium" 
                  onClick={(e) => {
                    e.stopPropagation();
                    onDeleteItem(item.id);
                  }}
                >
                  <IonIcon icon={trashOutline} />
                </IonButton>
                <IonReorder>
                  <IonIcon icon={menuOutline} color="medium" />
                </IonReorder>
              </div>
            </IonItem>
          ))}
        </IonReorderGroup>
      </IonList>

      {/* Done Items Header */}
      {doneItems.length > 0 && (
        <div style={{ 
          padding: '20px 16px 8px', 
          fontSize: '0.9rem', 
          color: 'var(--ion-color-medium)',
          fontWeight: 'bold',
          textTransform: 'uppercase',
          letterSpacing: '1px'
        }}>
          Completed
        </div>
      )}

      {/* Done Items (Static) */}
      <IonList lines="none">
        {doneItems.map((item) => (
          <IonItem key={item.id} style={{ '--background': 'transparent', opacity: 0.6 }}>
            <IonCheckbox 
              slot="start" 
              checked={item.done} 
              onIonChange={() => onToggleItemDone(item.id)}
              color="medium"
              style={{ '--size': '22px' }}
            />
            <IonLabel style={{ textDecoration: 'line-through' }}>{item.text}</IonLabel>
            <IonButton 
              slot="end"
              fill="clear" 
              color="medium" 
              onClick={(e) => {
                e.stopPropagation();
                onDeleteItem(item.id);
              }}
            >
              <IonIcon icon={trashOutline} />
            </IonButton>
          </IonItem>
        ))}
      </IonList>
    </>
  );
};

export default ListItems;