import { defineConfig, presetUno, presetAttributify, presetIcons, presetTypography, transformerDirectives, transformerVariantGroup } from 'unocss'

export default defineConfig({
    presets: [
        presetUno(),
        presetAttributify(),
        presetIcons({
            scale: 1.2,
            warn: true,
            cdn: 'https://esm.sh/'
        }),
        presetTypography()
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup()
    ],
    theme: {
        colors: {
            primary: '#B8FF2C',
            forge: {
                canvas: '#0B0C0A',
                surface: '#151713',
                raised: '#20231D',
                border: '#343A30',
                power: '#B8FF2C',
                heat: '#FF6A1A',
                cool: '#31D8C8'
            }
        },
        fontFamily: {
            sans: '"Alibaba PuHuiTi", "HarmonyOS Sans SC", "Source Han Sans SC", "Microsoft YaHei UI", sans-serif',
            display: '"Bahnschrift", "DIN Alternate", "Rajdhani", sans-serif'
        }
    },
    shortcuts: {
        'flex-center': 'flex justify-center items-center',
        'glass': 'bg-forge-surface/85 border border-forge-border',
        'ff-panel': 'bg-forge-surface border border-forge-border rounded-2 color-[#F3F1E8]'
    }
})
