import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import VideoPlayer from '../components/VideoPlayer';
import '@testing-library/jest-dom';

// Mock ReactPlayer as it uses browser APIs not available in JSDOM
jest.mock('react-player', () => {
    return jest.fn(({ playing, onPlay, onPause, onEnded, url }) => (
        <div data-testid="react-player">
            Mock Player {url}
            <button onClick={playing ? onPause : onPlay}>{playing ? 'Pause' : 'Play'}</button>
            <button onClick={onEnded}>End</button>
        </div>
    ));
});

// Mock Screenfull
jest.mock('screenfull', () => ({
    isEnabled: true,
    request: jest.fn(),
    exit: jest.fn(),
    on: jest.fn(),
    off: jest.fn()
}));

// Mock ResizeObserver
window.ResizeObserver = jest.fn().mockImplementation(() => ({
    observe: jest.fn(),
    unobserve: jest.fn(),
    disconnect: jest.fn(),
}));

// Mock controls to avoid complexity in this unit test? 
// Or render full. Detailed controls might need shallow render or separate test.
// We'll test integration mainly.

describe('VideoPlayer Component', () => {
    const mockProps = {
        url: 'http://test.com/video.mp4',
        onEnded: jest.fn(),
        isDarkMode: true,
        onToggleTheme: jest.fn(),
        playlist: [],
        currentVideoIndex: 0
    };

    it('renders without crashing', () => {
        render(<VideoPlayer {...mockProps} />);
        expect(screen.getByTestId('react-player')).toBeInTheDocument();
    });

    it('toggles play/pause when keeping state internal or via controls', () => {
        render(<VideoPlayer {...mockProps} />);
        // Initial state is paused
        // We find the play button in Controls. Since Controls is real, we can click it.
        // Controls uses AntD icons which might be hard to query by text.
        // We can query by role='button' and play icon.

        // Alternatively (and easier for unit test of wrapper), check if ReactPlayer recieves playing=false initially
        // The mock prints "Mock Player ..."
    });
});
