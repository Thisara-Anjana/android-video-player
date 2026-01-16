import React, { useState, useRef, useEffect } from 'react';
import ReactPlayer from 'react-player';
import screenfull from 'screenfull';
import { message } from 'antd';
import Controls from './Controls';
import { findDOMNode } from 'react-dom';
import { useKeyboardControls } from '../hooks/useKeyboardControls';

const VideoPlayer = ({
    url,
    onEnded,
    onNext,
    onPrevious,
    isDarkMode,
    onToggleTheme,
    playlist, // needed for next/prev logic if moved inside
    currentVideoIndex
}) => {
    const [playing, setPlaying] = useState(false);
    const [volume, setVolume] = useState(0.8);
    const [muted, setMuted] = useState(false);
    const [played, setPlayed] = useState(0); // 0 to 1
    const [loaded, setLoaded] = useState(0);
    const [duration, setDuration] = useState(0);
    const [playbackRate, setPlaybackRate] = useState(1.0);
    const [isFullscreen, setIsFullscreen] = useState(false);
    const [seeking, setSeeking] = useState(false);

    const playerRef = useRef(null);
    const playerContainerRef = useRef(null);

    const handlePlayPause = () => setPlaying(!playing);
    const handleStop = () => {
        setPlaying(false);
        setPlayed(0);
        playerRef.current?.seekTo(0);
    };

    const handleToggleMute = () => setMuted(!muted);

    const handleVolumeChange = (newVolume) => {
        setVolume(parseFloat(newVolume));
        setMuted(newVolume === 0);
    };

    const handleProgress = (state) => {
        // We only update if not seeking
        if (!seeking) {
            setPlayed(state.played);
            setLoaded(state.loaded);
        }
    };

    const handleDuration = (duration) => {
        setDuration(duration);
    };

    const handleSeekChange = (value) => {
        setSeeking(true);
        setPlayed(parseFloat(value));
    };

    const handleSeek = (value) => {
        setSeeking(false);
        playerRef.current?.seekTo(parseFloat(value));
    };

    const handleEnded = () => {
        setPlaying(false);
        if (onEnded) onEnded();
    };

    const toggleFullscreen = () => {
        if (screenfull.isEnabled && playerContainerRef.current) {
            screenfull.toggle(playerContainerRef.current);
        }
    };

    useEffect(() => {
        if (screenfull.isEnabled) {
            const handleChange = () => setIsFullscreen(screenfull.isFullscreen);
            screenfull.on('change', handleChange);
            return () => screenfull.off('change', handleChange);
        }
    }, []);

    // Keyboard controls
    // Handlers for keyboard
    const handleKeyboardSeekForward = () => {
        if (playerRef.current) {
            const currentTime = playerRef.current.getCurrentTime();
            playerRef.current.seekTo(currentTime + 5);
        }
    };

    const handleKeyboardSeekBackward = () => {
        if (playerRef.current) {
            const currentTime = playerRef.current.getCurrentTime();
            playerRef.current.seekTo(currentTime - 5);
        }
    };

    const handleKeyboardVolumeUp = () => {
        setVolume(prev => Math.min(prev + 0.1, 1));
        setMuted(false);
    };

    const handleKeyboardVolumeDown = () => {
        setVolume(prev => Math.max(prev - 0.1, 0));
    };

    useKeyboardControls({
        onPlayPause: handlePlayPause,
        onSeekForward: handleKeyboardSeekForward,
        onSeekBackward: handleKeyboardSeekBackward,
        onVolumeUp: handleKeyboardVolumeUp,
        onVolumeDown: handleKeyboardVolumeDown,
        onToggleFullscreen: toggleFullscreen
    });

    // Expose controls for keyboard hook in parent or here
    // For now simple handlers passed to Controls

    return (
        <div
            ref={playerContainerRef}
            style={{
                position: 'relative',
                width: '100%',
                height: isFullscreen ? '100vh' : 'auto',
                background: '#000',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center'
            }}
        >
            <div
                style={{
                    flex: 1,
                    position: 'relative',
                    minHeight: isFullscreen ? '100%' : '480px' // Aspect ratio handling needed
                }}
                onClick={handlePlayPause}
            >
                <ReactPlayer
                    ref={playerRef}
                    url={url}
                    width="100%"
                    height="100%"
                    playing={playing}
                    volume={volume}
                    muted={muted}
                    playbackRate={playbackRate}
                    onProgress={handleProgress}
                    onDuration={handleDuration}
                    onEnded={handleEnded}
                    onError={(e) => message.error("Error playing video")}
                    style={{ position: 'absolute', top: 0, left: 0 }}
                    config={{
                        file: {
                            attributes: {
                                crossOrigin: 'anonymous' // for subtitles etc
                            }
                        }
                    }}
                />
            </div>

            <div style={{
                position: isFullscreen ? 'absolute' : 'relative',
                bottom: 0,
                left: 0,
                right: 0,
                background: isDarkMode ? 'transparent' : '#f0f2f5',
                zIndex: 10,
                padding: isFullscreen ? '20px' : '0'
            }}>
                <Controls
                    playing={playing}
                    played={played}
                    duration={duration}
                    currentTime={duration * played}
                    volume={volume}
                    muted={muted}
                    playbackRate={playbackRate}
                    isFullscreen={isFullscreen}
                    onPlayPause={handlePlayPause}
                    onStop={handleStop}
                    onSeek={handleSeek}
                    onSeekChange={handleSeekChange}
                    onVolumeChange={handleVolumeChange}
                    onToggleMute={handleToggleMute}
                    onToggleFullscreen={toggleFullscreen}
                    onPlaybackRateChange={setPlaybackRate}
                    isDarkMode={isDarkMode}
                    onToggleTheme={onToggleTheme}
                />
            </div>
        </div>
    );
};

export default VideoPlayer;
