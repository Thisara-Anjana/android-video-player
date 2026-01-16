import React from 'react';
import { Menu, Dropdown, Button, Switch, Space, Typography } from 'antd';
import { SettingOutlined, BulbOutlined, BulbFilled } from '@ant-design/icons';

const { Text } = Typography;

const SettingsMenu = ({ playbackRate, onPlaybackRateChange, isDarkMode, onToggleTheme }) => {
    const speedOptions = [0.5, 1, 1.5, 2];

    const menuItems = [
        {
            key: 'speed',
            label: 'Playback Speed',
            children: speedOptions.map((speed) => ({
                key: speed.toString(),
                label: `${speed}x`,
                onClick: () => onPlaybackRateChange(speed),
                style: { fontWeight: playbackRate === speed ? 'bold' : 'normal' },
            })),
        },
        {
            key: 'theme',
            label: (
                <Space onClick={(e) => e.stopPropagation()}>
                    <Text>Dark Mode</Text>
                    <Switch
                        checked={isDarkMode}
                        onChange={onToggleTheme}
                        checkedChildren={<BulbFilled />}
                        unCheckedChildren={<BulbOutlined />}
                        size="small"
                    />
                </Space>
            ),
        },
    ];

    return (
        <Dropdown menu={{ items: menuItems }} trigger={['click']} placement="topRight">
            <Button type="text" icon={<SettingOutlined />} style={{ color: 'inherit' }} />
        </Dropdown>
    );
};

export default SettingsMenu;
