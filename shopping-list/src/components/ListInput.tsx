import React, { useState, useRef } from 'react';
import { IonItem, IonInput, IonButton, IonIcon } from '@ionic/react';
import { addCircle } from 'ionicons/icons';
import './ListInput.css';

interface ListInputProps {
  onAddItem: (text: string) => void;
}

const ListInput: React.FC<ListInputProps> = ({ onAddItem }) => {
  const [text, setText] = useState('');
  const inputRef = useRef<HTMLIonInputElement>(null);

  const handleAdd = () => {
    if (text.trim()) {
      onAddItem(text.trim());
      setText('');
      // Keep focus for rapid entry
      inputRef.current?.setFocus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleAdd();
    }
  };

  return (
    <div style={{ padding: '16px 16px 8px 16px' }}>
      <IonItem lines="none" style={{ 
        borderRadius: '12px', 
        boxShadow: '0 4px 10px rgba(0,0,0,0.05)',
        '--background': 'var(--ion-color-step-50)'
      }}>
        <IonInput
          className="list-input"
          ref={inputRef}
          value={text}
          placeholder="Add item..."
          onIonInput={e => setText(e.detail.value!)}
          onKeyDown={handleKeyDown}
          enterkeyhint="enter"
        />
        <IonButton fill="clear" slot="end" onClick={handleAdd}>
          <IonIcon icon={addCircle} size="large" color="primary" />
        </IonButton>
      </IonItem>
    </div>
  );
};

export default ListInput;