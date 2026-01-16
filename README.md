# Android Video Player

A responsive web-based video player built with React and Ant Design, featuring local file playback, playlists, custom controls, and keyboard shortcuts.

## Features
- **Local File Playback**: Load video files from your device.
- **Custom Controls**: Play/Pause, Seek, Volume, Mute, Fullscreen, Playback Speed.
- **Playlist**: Manage multiple videos in a playlist sidebar.
- **Themes**: Toggle between Light and Dark modes.
- **Keyboard Shortcuts**:
    - `Space` / `K`: Play/Pause
    - `Arrow Left/Right`: Seek
    - `Arrow Up/Down`: Volume
    - `F`: Toggle Fullscreen

## Installation

1. Clone or download the repository.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Build for production:
   ```bash
   npm run build
   ```
5. Run tests:
   ```bash
   npm test
   ```

## Tech Stack
- React
- Vite
- Ant Design
- React Player
- Jest + React Testing Library

## Structure
- `src/components`: UI components (VideoPlayer, Controls, etc.)
- `src/hooks`: Custom hooks (useKeyboardControls)
- `src/utils`: Helper functions
- `src/__tests__`: Unit and integration tests
