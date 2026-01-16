import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import App from '../App';
import '@testing-library/jest-dom';

// Mocks
jest.mock('react-player', () => {
    return ({ playing }) => <div data-testid="player">{playing ? 'Playing' : 'Paused'}</div>;
});

window.ResizeObserver = jest.fn().mockImplementation(() => ({
    observe: jest.fn(),
    unobserve: jest.fn(),
    disconnect: jest.fn(),
}));

Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: jest.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // deprecated
        removeListener: jest.fn(), // deprecated
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
    })),
});

describe('App Integration', () => {
    it('renders Main Title', () => {
        render(<App />);
        expect(screen.getByText(/Video Player/i)).toBeInTheDocument();
    });

    it('shows "No Video Selected" initially', () => {
        render(<App />);
        expect(screen.getByText(/No Video Selected/i)).toBeInTheDocument();
    });
});
