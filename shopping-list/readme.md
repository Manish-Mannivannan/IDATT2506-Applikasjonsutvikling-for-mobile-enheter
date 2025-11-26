Shopping List App – Ionic + React + Capacitor  
Cross-Platform Mobile Application (Android + Web)

This document explains how to install, run, and test the Shopping List application on both the web and an Android emulator. It also includes information about the development environment and expected functionality.

------------------------------------------------------------

1. Overview

This project is a cross-platform shopping/todo list application built using:
- Ionic Framework (v7)
- React
- Capacitor
- TypeScript

The application includes:
- Multiple lists
- Adding items quickly with Enter
- Marking items as done/undone
- Drag-and-drop item reordering
- Deleting lists and individual items
- Persistent JSON storage
- Dark/light mode support
- Ability to run on both web and Android devices/emulators

------------------------------------------------------------

2. Requirements

Software Needed:
- Node.js (LTS recommended)
- Ionic CLI
- Capacitor CLI (installed with Ionic)
- Android Studio (for Android emulator)

Install dependencies:
npm install

------------------------------------------------------------

3. Running the Web Version

To run the development server in your browser:
ionic serve

The application will open automatically at:
http://localhost:8100/

This version is useful for rapid iteration during development.

------------------------------------------------------------

4. Building for Android

Generate a production build:
npm run build

Then sync the web build and native plugins:
npx cap sync android

------------------------------------------------------------

5. Running on Android Emulator

Open the Android project in Android Studio:
npx cap open android

Steps in Android Studio:
1. Open Device Manager
2. Select or create a virtual device (Pixel series recommended)
3. Click Run (the ▶ button)
4. The app installs and launches on the emulator

------------------------------------------------------------

6. Tested Android Environment

Fill in these values based on your emulator:
- Android Studio Version:
- Android Emulator Device (e.g., Pixel 7):
- Android Version (e.g., Android 14):
- API Level (e.g., API 34):
- Capacitor Android Runtime Version (e.g., @capacitor/android 6.x):

How to check the API level:
1. Open Android Studio
2. Go to Device Manager
3. Look for something like: 
   Pixel 7 — Android 14 (API 34)

This is the API level you should report.

------------------------------------------------------------

7. Testing the Application

Verify the following features:

List Management:
- Create new lists
- Switch between lists
- Delete a list

Item Management:
- Add items
- Press Enter to add multiple items quickly
- Toggle items between done/undone
- Delete individual items
- Completed items appear in a separate section

Reordering:
- Drag active items up/down using the reorder handle
- Order updates immediately

Persistence:
- Data remains after closing and reopening the app
- Data persists after emulator reboot
- JSON file storage is handled automatically by Capacitor

------------------------------------------------------------

8. Build Commands Summary

Install dependencies: npm install
Run in browser: ionic serve
Build web assets: npm run build
Sync Android project: npx cap sync android
Open Android Studio: npx cap open android
Run the app: Use the Run (▶) button inside Android Studio

------------------------------------------------------------

9. Project Structure (Simplified)

src/components/ListTabs.tsx
src/components/ListInput.tsx
src/components/ListItems.tsx
src/hooks/useShoppingLists.ts
src/utils/storage.ts
src/pages/ShoppingListsPage.tsx
src/types.ts
src/App.tsx

------------------------------------------------------------

10. Storage Details

- Lists are saved in a single file: shopping-lists.json
- Stored using Capacitor Filesystem API
- On Web: stored using IndexedDB
- On Android: stored in the app’s private internal storage
- No manual folder creation is required

------------------------------------------------------------

11. License

This project was created as part of a university assignment in cross-platform mobile development.