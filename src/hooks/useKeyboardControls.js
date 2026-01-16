import { useEffect } from 'react';

/**
 * Custom hook for keyboard controls
 * @param {Object} handlers - Object containing handler functions
 * @param {Function} handlers.onPlayPause - Toggle play/pause
 * @param {Function} handlers.onSeekForward - Seek forward 5s
 * @param {Function} handlers.onSeekBackward - Seek backward 5s
 * @param {Function} handlers.onVolumeUp - Increase volume
 * @param {Function} handlers.onVolumeDown - Decrease volume
 * @param {Function} handlers.onToggleFullscreen - Toggle fullscreen
 */
export const useKeyboardControls = ({
    onPlayPause,
    onSeekForward,
    onSeekBackward,
    onVolumeUp,
    onVolumeDown,
    onToggleFullscreen,
}) => {
    useEffect(() => {
        const handleKeyDown = (e) => {
            // Ignore if user is typing in an input
            if (['INPUT', 'TEXTAREA'].includes(e.target.tagName)) return;

            switch (e.code) {
                case 'Space':
                case 'k':
                case 'K':
                    e.preventDefault();
                    onPlayPause?.();
                    break;
                case 'ArrowRight':
                    e.preventDefault();
                    onSeekForward?.();
                    break;
                case 'ArrowLeft':
                    e.preventDefault();
                    onSeekBackward?.();
                    break;
                case 'ArrowUp':
                    e.preventDefault();
                    onVolumeUp?.();
                    break;
                case 'ArrowDown':
                    e.preventDefault();
                    onVolumeDown?.();
                    break;
                case 'KeyF':
                    e.preventDefault();
                    onToggleFullscreen?.();
                    break;
                default:
                    break;
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [onPlayPause, onSeekForward, onSeekBackward, onVolumeUp, onVolumeDown, onToggleFullscreen]);
};
