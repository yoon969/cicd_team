// postbuild.js
import fs from 'fs';
import path from 'path';

const source = path.resolve('..', 'src', 'main', 'resources', 'static', 'notice', '.vite', 'manifest.json');
const target = path.resolve('..', 'src', 'main', 'resources', 'static', 'notice', 'assets', 'manifest.json');

// 디렉토리 없으면 생성
fs.mkdirSync(path.dirname(target), { recursive: true });

// 복사
fs.copyFileSync(source, target);

console.log(`✅ manifest.json copied to: ${target}`);
