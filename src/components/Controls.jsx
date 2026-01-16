import React from 'react';
import { Button, Slider, Row, Col, Space, Typography, Tooltip } from 'antd';
import {
    PlayCircleFilled,
    PauseCircleFilled,
    StepBackwardOutlined,
    StepForwardOutlined,
    SoundOutlined,
    AudioMutedOutlined,
    FullscreenOutlined,
    FullscreenExitOutlined,
    RedoOutlined
} from '@ant-design/icons';
import { formatTime } from '../utils/formatTime';
import SettingsMenu from './SettingsMenu';

const { Text } = Typography;

const Controls = ({
    playing,
    played, // fraction 0-1
    duration, // seconds
    currentTime, // seconds
    volume, // fraction 0-1
    muted,
    playbackRate,
    isFullscreen,
    onPlayPause,
    onStop,
    onSeek,
    onSeekChange, // while dragging
    onVolumeChange,
    onToggleMute,
    onToggleFullscreen,
    onPlaybackRateChange,
    isDarkMode,
    onToggleTheme,
    title
}) => {
    return (
        <div className={`controls-container ${isDarkMode ? 'dark' : 'light'}`} style={{ padding: '10px', background: isDarkMode ? 'rgba(0,0,0,0.7)' : 'rgba(255,255,255,0.9)', borderRadius: '8px', backdropFilter: 'blur(5px)' }}>
            {/* Progress Bar */}
            <Row align="middle">
                <Col span={24}>
                    <Slider
                        min={0}
                        max={1}
                        step={0.001}
                        value={played}
                        onChange={onSeekChange}
                        onAfterChange={onSeek}
                        tooltip={{ formatter: null }} // Custom tooltip logic can be added here
                        trackStyle={{ backgroundColor: '#1890ff' }}
                        handleStyle={{ borderColor: '#1890ff' }}
                    />
                </Col>
            </Row>

            <Row align="middle" justify="space-between" gutter={[8, 8]}>
                {/* Left Controls */}
                <Col>
                    <Space size="small">
                        <Tooltip title="Previous (Playlist)">
                            <Button type="text" icon={<StepBackwardOutlined />} onClick={() => {/* Handle prev in parent */ }} disabled style={{ color: 'inherit' }} />
                        </Tooltip>

                        <Button
                            type="text"
                            icon={playing ? <PauseCircleFilled style={{ fontSize: '24px' }} /> : <PlayCircleFilled style={{ fontSize: '24px' }} />}
                            onClick={onPlayPause}
                            style={{ color: '#1890ff' }}
                        />

                        <Tooltip title="Stop">
                            <Button type="text" icon={<RedoOutlined rotate={270} />} onClick={onStop} style={{ color: 'inherit' }} />
                        </Tooltip>

                        <Tooltip title="Next (Playlist)">
                            <Button type="text" icon={<StepForwardOutlined />} onClick={() => {/* Handle next in parent */ }} disabled style={{ color: 'inherit' }} />
                        </Tooltip>

                        <Space size={4} style={{ marginLeft: 8 }}>
                            <div style={{ width: 80, display: 'flex', alignItems: 'center' }}>
                                <Button type="text" icon={muted || volume === 0 ? <AudioMutedOutlined /> : <SoundOutlined />} onClick={onToggleMute} style={{ color: 'inherit' }} />
                                <Slider
                                    min={0}
                                    max={1}
                                    step={0.1}
                                    value={muted ? 0 : volume}
                                    onChange={onVolumeChange}
                                    style={{ width: 50, marginLeft: 4 }}
                                    trackStyle={{ backgroundColor: '#1890ff' }}
                                />
                            </div>
                        </Space>

                        <Text style={{ color: 'inherit', marginLeft: 8, fontSize: '12px' }}>
                            {formatTime(currentTime)} / {formatTime(duration)}
                        </Text>
                    </Space>
                </Col>

                {/* Right Controls */}
                <Col>
                    <Space size="small">
                        <SettingsMenu
                            playbackRate={playbackRate}
                            onPlaybackRateChange={onPlaybackRateChange}
                            isDarkMode={isDarkMode}
                            onToggleTheme={onToggleTheme}
                        />
                        <Button
                            type="text"
                            icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
                            onClick={onToggleFullscreen}
                            style={{ color: 'inherit' }}
                        />
                    </Space>
                </Col>
            </Row>
        </div>
    );
};

export default Controls;
