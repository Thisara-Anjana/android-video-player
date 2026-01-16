import React from 'react';
import { List, Button, Typography, Card, Empty, Upload } from 'antd';
import { PlayCircleOutlined, UploadOutlined, FileAddOutlined } from '@ant-design/icons';

const { Text, Title } = Typography;

const Playlist = ({ playlist, currentVideoIndex, onVideoSelect, onAddFiles }) => {
    return (
        <Card
            title={<Title level={5} style={{ margin: 0 }}>Playlist</Title>}
            extra={
                <div style={{ position: 'relative', overflow: 'hidden', display: 'inline-block' }}>
                    <Button icon={<FileAddOutlined />}>Add Files</Button>
                    <input
                        type="file"
                        multiple
                        accept="video/*"
                        onChange={onAddFiles}
                        style={{ position: 'absolute', left: 0, top: 0, opacity: 0, width: '100%', height: '100%', cursor: 'pointer' }}
                    />
                </div>
            }
            bodyStyle={{ padding: 0, height: '100%', overflowY: 'auto', maxHeight: '400px' }}
            style={{ height: '100%', display: 'flex', flexDirection: 'column' }}
        >
            {playlist.length === 0 ? (
                <Empty description="No videos added" style={{ padding: '20px' }} />
            ) : (
                <List
                    dataSource={playlist}
                    renderItem={(item, index) => (
                        <List.Item
                            onClick={() => onVideoSelect(index)}
                            style={{
                                cursor: 'pointer',
                                backgroundColor: index === currentVideoIndex ? 'var(--primary-color-faded, rgba(24, 144, 255, 0.1))' : 'transparent',
                                padding: '12px 16px',
                                transition: 'background 0.3s'
                            }}
                            className="playlist-item"
                        >
                            <List.Item.Meta
                                avatar={<PlayCircleOutlined style={{ color: index === currentVideoIndex ? '#1890ff' : 'inherit' }} />}
                                title={<Text strong={index === currentVideoIndex} ellipsis>{item.name}</Text>}
                                description={<Text type="secondary" style={{ fontSize: '10px' }}>Local File</Text>}
                            />
                        </List.Item>
                    )}
                />
            )}
        </Card>
    );
};

export default Playlist;
