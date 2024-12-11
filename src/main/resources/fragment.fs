#version 330

in vec2 outTexCoord;
in float outLight;

out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{
    vec4 color = texture(texture_sampler, outTexCoord);
    fragColor = vec4(outLight * color.xyz, color.w);
}
