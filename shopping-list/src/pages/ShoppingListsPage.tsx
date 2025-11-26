import React from 'react';
import { 
  IonPage, 
  IonHeader, 
  IonToolbar, 
  IonTitle, 
  IonContent, 
  IonAlert,
  IonButton,
  IonIcon
} from '@ionic/react';
import { add } from 'ionicons/icons';
import { useShoppingLists } from '../hooks/useShoppingLists';
import ListTabs from '../components/ListTabs';
import ListInput from '../components/ListInput';
import ListItems from '../components/ListItems';

const ShoppingListsPage: React.FC = () => {
  const { 
    lists, 
    activeListId, 
    createList, 
    deleteList, 
    addItem, 
    toggleItemDone, 
    updateListItems,
    setActiveListId
  } = useShoppingLists();

  const [showAddAlert, setShowAddAlert] = React.useState(false);

  const activeList = lists.find(l => l.id === activeListId);

  return (
    <IonPage>
      <IonHeader translucent={true} className="ion-no-border">
        <IonToolbar>
          <IonTitle>Shopping List</IonTitle>
        </IonToolbar>
        <ListTabs 
          lists={lists}
          activeListId={activeListId}
          onSelectList={setActiveListId}
          onAddList={() => setShowAddAlert(true)}
          onDeleteList={deleteList}
        />
      </IonHeader>

      <IonContent fullscreen className="ion-padding-bottom">
        {!activeList ? (
          <div style={{ 
            display: 'flex', 
            flexDirection: 'column', 
            alignItems: 'center', 
            justifyContent: 'center', 
            height: '80%',
            opacity: 0.7
          }}>
            <h3>No lists yet</h3>
            <p>Create a list to get started</p>
            <IonButton onClick={() => setShowAddAlert(true)} shape="round" className="ion-margin-top">
              <IonIcon icon={add} slot="start" />
              Create List
            </IonButton>
          </div>
        ) : (
          <>
            <ListInput onAddItem={(text) => addItem(activeList.id, text)} />
            <ListItems 
              items={activeList.items}
              onToggleItemDone={(itemId) => toggleItemDone(activeList.id, itemId)}
              onUpdateItems={(items) => updateListItems(activeList.id, items)}
            />
          </>
        )}

        <IonAlert
          isOpen={showAddAlert}
          onDidDismiss={() => setShowAddAlert(false)}
          header="New List"
          inputs={[
            {
              name: 'name',
              type: 'text',
              placeholder: 'List Name (e.g. Groceries)'
            }
          ]}
          buttons={[
            {
              text: 'Cancel',
              role: 'cancel',
              handler: () => console.log('Confirm Cancel')
            },
            {
              text: 'Create',
              handler: (data) => {
                if (data.name) createList(data.name);
              }
            }
          ]}
        />
      </IonContent>
    </IonPage>
  );
};

export default ShoppingListsPage;