import React, { useState } from 'react';
import { Layout, ConfigProvider, theme } from 'antd';
import VideoPlayer from './components/VideoPlayer';
import Playlist from './components/Playlist';
import './index.css';

const { Header, Content, Sider } = Layout;
const { defaultAlgorithm, darkAlgorithm } = theme;

const App = () => {
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [currentVideoUrl, setCurrentVideoUrl] = useState(null);
  const [playlist, setPlaylist] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(-1);

  const handleToggleTheme = () => setIsDarkMode(!isDarkMode);

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    const newVideos = files.map((file) => ({
      name: file.name,
      url: URL.createObjectURL(file),
      type: 'local'
    }));

    setPlaylist((prev) => [...prev, ...newVideos]);

    // If no video playing, start the first one
    if (!currentVideoUrl && newVideos.length > 0) {
      setCurrentVideoUrl(newVideos[0].url);
      setCurrentIndex(playlist.length); // Index of first new video
    }
  };

  const handleVideoSelect = (index) => {
    setCurrentIndex(index);
    setCurrentVideoUrl(playlist[index].url);
  };

  const handleVideoEnd = () => {
    // Auto play next
    if (currentIndex < playlist.length - 1) {
      handleVideoSelect(currentIndex + 1);
    }
  };

  return (
    <ConfigProvider
      theme={{
        algorithm: isDarkMode ? darkAlgorithm : defaultAlgorithm,
        token: {
          colorPrimary: '#1890ff',
        },
      }}
    >
      <Layout style={{ minHeight: '100vh' }}>
        <Header style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '0 20px',
          background: isDarkMode ? '#001529' : '#fff',
          borderBottom: isDarkMode ? 'none' : '1px solid #f0f0f0'
        }}>
          <h1 style={{
            color: isDarkMode ? '#fff' : '#000',
            margin: 0,
            fontSize: '20px'
          }}>
            Video Player
          </h1>
        </Header>
        <Layout>
          <Content style={{ margin: '0' }}>
            {currentVideoUrl ? (
              <VideoPlayer
                url={currentVideoUrl}
                isDarkMode={isDarkMode}
                onToggleTheme={handleToggleTheme}
                onEnded={handleVideoEnd}
                playlist={playlist}
                currentVideoIndex={currentIndex}
              />
            ) : (
              <div style={{
                height: '100%',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                flexDirection: 'column',
                background: isDarkMode ? '#141414' : '#fafafa'
              }}>
                <h2>No Video Selected</h2>
                <p>Add files to the playlist to begin.</p>
              </div>
            )}
          </Content>
          <Sider
            width={300}
            theme={isDarkMode ? 'dark' : 'light'}
            breakpoint="lg"
            collapsedWidth="0"
            style={{
              borderLeft: isDarkMode ? '1px solid #303030' : '1px solid #f0f0f0',
              overflow: 'auto',
              height: '100vh',
              position: 'fixed',
              right: 0,
              top: 64,
              bottom: 0
            }}
          >
            <Playlist
              playlist={playlist}
              currentVideoIndex={currentIndex}
              onVideoSelect={handleVideoSelect}
              onAddFiles={handleFileChange}
            />
          </Sider>
        </Layout>
      </Layout>
    </ConfigProvider>
  );
};

export default App;
